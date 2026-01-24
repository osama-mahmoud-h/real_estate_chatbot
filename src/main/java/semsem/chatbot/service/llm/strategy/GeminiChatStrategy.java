package semsem.chatbot.service.llm.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import semsem.chatbot.config.LLMProperties;
import semsem.chatbot.config.llm.ProviderConfig;
import semsem.chatbot.config.llm.chat.ChatModelConfig;
import semsem.chatbot.model.enums.LLMProvider;
import semsem.chatbot.service.llm.dto.LLMRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini Chat LLM Strategy.
 * Single Responsibility: knows only how to call Gemini API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiChatStrategy extends BaseChatLLMStrategy {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final LLMProperties llmProperties;
    private WebClient webClient;

    @PostConstruct
    private void initWebClient() {
        String baseUrl = getProviderConfig().getBaseUrl();
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private ProviderConfig getProviderConfig() {
        return llmProperties.getProviderOrThrow("gemini");
    }

    private ChatModelConfig getModelConfig() {
        String modelName = llmProperties.getChat().getModel();
        return getProviderConfig().getChatModel(modelName).orElse(null);
    }

    // =========================================================================
    // URL BUILDERS
    // =========================================================================

    private String buildGenerateContentUrl() {
        String model = llmProperties.getChat().getModel();
        String apiKey = getProviderConfig().getApiKey();
        return "/v1beta/models/" + model + ":generateContent?key=" + apiKey;
    }

    private String buildStreamUrl() {
        String model = llmProperties.getChat().getModel();
        String apiKey = getProviderConfig().getApiKey();
        return "/v1beta/models/" + model + ":streamGenerateContent?key=" + apiKey + "&alt=sse";
    }

    // =========================================================================
    // CONTENT MAPPERS
    // =========================================================================

    private List<Map<String, Object>> promptToContents(String prompt) {
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part), "role", "user");
        return List.of(content);
    }

    private List<Map<String, Object>> messagesToContents(List<Map<String, String>> messages) {
        return messages.stream().map(msg -> {
            String role = msg.getOrDefault("role", "user");
            String text = msg.getOrDefault("content", "");
            String geminiRole = "assistant".equalsIgnoreCase(role) ? "model" : role;
            Map<String, Object> part = Map.of("text", text);
            return Map.<String, Object>of("parts", List.of(part), "role", geminiRole);
        }).toList();
    }

    // =========================================================================
    // REQUEST BODY BUILDERS
    // =========================================================================

    private Map<String, Object> buildRequestBody(List<Map<String, Object>> contents, Map<String, Object> options) {
        double temperature = getOptionDouble(options, "temperature", llmProperties.getChat().getTemperature());
        int maxTokens = getOptionInt(options, "maxTokens", llmProperties.getChat().getMaxTokens());
        double topP = getOptionDouble(options, "topP", llmProperties.getChat().getTopP());

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", temperature);
        generationConfig.put("maxOutputTokens", maxTokens);
        generationConfig.put("topP", topP);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", contents);
        body.put("generationConfig", generationConfig);
        return body;
    }

    private Map<String, Object> buildRequestBodyFromLLMRequest(List<Map<String, Object>> contents, LLMRequest request) {
        double temperature = request.getTemperature() != null ? request.getTemperature() : llmProperties.getChat().getTemperature();
        int maxTokens = request.getMaxTokens() != null ? request.getMaxTokens() : llmProperties.getChat().getMaxTokens();
        double topP = request.getTopP() != null ? request.getTopP() : llmProperties.getChat().getTopP();

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", temperature);
        generationConfig.put("maxOutputTokens", maxTokens);
        generationConfig.put("topP", topP);

        if (request.getStopSequences() != null && !request.getStopSequences().isEmpty()) {
            generationConfig.put("stopSequences", request.getStopSequences());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", contents);
        body.put("generationConfig", generationConfig);

        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            Map<String, Object> systemPart = Map.of("text", request.getSystemPrompt());
            body.put("system_instruction", Map.of("parts", List.of(systemPart)));
        }

        return body;
    }

    // =========================================================================
    // RESPONSE PARSER
    // =========================================================================

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> responseBody) {
        if (responseBody == null) return "";
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "";
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        if (content == null) return "";
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) return "";
        Object text = parts.get(0).get("text");
        return text != null ? text.toString() : "";
    }

    // =========================================================================
    // OPTION HELPERS
    // =========================================================================

    private double getOptionDouble(Map<String, Object> options, String key, double defaultValue) {
        if (options == null || !options.containsKey(key)) return defaultValue;
        Object val = options.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (NumberFormatException e) { return defaultValue; }
    }

    private int getOptionInt(Map<String, Object> options, String key, int defaultValue) {
        if (options == null || !options.containsKey(key)) return defaultValue;
        Object val = options.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        try { return Integer.parseInt(val.toString()); } catch (NumberFormatException e) { return defaultValue; }
    }

    // =========================================================================
    // INTERFACE IMPLEMENTATIONS
    // =========================================================================

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        log.debug("Generating with Gemini model: {}", llmProperties.getChat().getModel());
        try {
            List<Map<String, Object>> contents = promptToContents(prompt);
            Map<String, Object> requestBody = buildRequestBody(contents, options);

            Map<String, Object> response = webClient.post()
                    .uri(buildGenerateContentUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            String text = extractTextFromResponse(response);
            log.debug("Gemini response length: {}", text.length());
            return text;
        } catch (Exception e) {
            log.error("Gemini generate failed: {}", e.getMessage(), e);
            return "";
        }
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        log.debug("Streaming with Gemini model: {}", llmProperties.getChat().getModel());
        try {
            List<Map<String, Object>> contents = promptToContents(prompt);
            Map<String, Object> requestBody = buildRequestBody(contents, options);

            return webClient.post()
                    .uri(buildStreamUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .filter(chunk -> chunk != null && !chunk.isBlank())
                    .map(chunk -> {
                        try {
                            Map<String, Object> parsed = objectMapper.readValue(chunk,
                                    new TypeReference<Map<String, Object>>() {});
                            return extractTextFromResponse(parsed);
                        } catch (Exception e) {
                            log.warn("Failed to parse streaming chunk: {}", e.getMessage());
                            return "";
                        }
                    })
                    .filter(text -> !text.isEmpty())
                    .onErrorResume(e -> {
                        log.error("Gemini stream failed: {}", e.getMessage(), e);
                        return Flux.empty();
                    });
        } catch (Exception e) {
            log.error("Gemini stream setup failed: {}", e.getMessage(), e);
            return Flux.empty();
        }
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        log.debug("Chat with Gemini model: {}, messages: {}", llmProperties.getChat().getModel(), messages.size());
        try {
            List<Map<String, Object>> contents = messagesToContents(messages);
            Map<String, Object> requestBody = buildRequestBody(contents, options);

            Map<String, Object> response = webClient.post()
                    .uri(buildGenerateContentUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Gemini chat failed: {}", e.getMessage(), e);
            return "";
        }
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        log.debug("Chat streaming with Gemini model: {}", llmProperties.getChat().getModel());
        try {
            List<Map<String, Object>> contents = messagesToContents(messages);
            Map<String, Object> requestBody = buildRequestBody(contents, Map.of());

            return webClient.post()
                    .uri(buildStreamUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .filter(chunk -> chunk != null && !chunk.isBlank())
                    .map(chunk -> {
                        try {
                            Map<String, Object> parsed = objectMapper.readValue(chunk,
                                    new TypeReference<Map<String, Object>>() {});
                            return extractTextFromResponse(parsed);
                        } catch (Exception e) {
                            log.warn("Failed to parse streaming chunk: {}", e.getMessage());
                            return "";
                        }
                    })
                    .filter(text -> !text.isEmpty())
                    .onErrorResume(e -> {
                        log.error("Gemini chat stream failed: {}", e.getMessage(), e);
                        return Flux.empty();
                    });
        } catch (Exception e) {
            log.error("Gemini chat stream setup failed: {}", e.getMessage(), e);
            return Flux.empty();
        }
    }

    @Override
    protected String doGenerate(LLMRequest request) {
        log.debug("doGenerate with Gemini model: {}", llmProperties.getChat().getModel());
        try {
            List<Map<String, Object>> contents;
            if (request.getMessages() != null && !request.getMessages().isEmpty()) {
                contents = messagesToContents(request.getMessages());
            } else {
                String prompt = request.getPrompt() != null ? request.getPrompt() : "";
                contents = promptToContents(prompt);
            }

            Map<String, Object> requestBody = buildRequestBodyFromLLMRequest(contents, request);

            Map<String, Object> response = webClient.post()
                    .uri(buildGenerateContentUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Gemini doGenerate failed: {}", e.getMessage(), e);
            return "";
        }
    }

    // =========================================================================
    // IDENTITY
    // =========================================================================

    @Override
    public LLMProvider getProvider() {
        return LLMProvider.GEMINI;
    }

    @Override
    public String getModelName() {
        return llmProperties.getChat().getModel();
    }

    @Override
    public boolean isAvailable() {
        return llmProperties.getProvider("gemini")
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }
}