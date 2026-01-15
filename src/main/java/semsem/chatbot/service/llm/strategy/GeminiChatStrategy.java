package semsem.chatbot.service.llm.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import semsem.chatbot.model.enums.LLMProvider;
import semsem.chatbot.service.llm.dto.LLMRequest;

import java.util.List;
import java.util.Map;

/**
 * Google Gemini Chat LLM Strategy (Cloud).
 */
@Slf4j
@Component
public class GeminiChatStrategy extends BaseChatLLMStrategy {

    @Value("${llm.gemini.api-key:}")
    private String apiKey;

    @Value("${llm.gemini.model:gemini-pro}")
    private String model;

    @Value("${llm.gemini.base-url:https://generativelanguage.googleapis.com}")
    private String baseUrl;

    // TODO: Inject Spring AI GoogleGenAI ChatModel or WebClient

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        // TODO: Implement using Google Gemini API
        log.debug("Generating with Gemini model: {}", model);
        return "";
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        // TODO: Implement streaming using Google Gemini API
        log.debug("Streaming with Gemini model: {}", model);
        return Flux.empty();
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        // TODO: Implement chat using Google Gemini API
        log.debug("Chat with Gemini model: {}", model);
        return "";
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        // TODO: Implement streaming chat using Google Gemini API
        log.debug("Chat streaming with Gemini model: {}", model);
        return Flux.empty();
    }

    @Override
    protected String doGenerate(LLMRequest request) {
        // TODO: Implement
        return "";
    }

    @Override
    public LLMProvider getProvider() {
        return LLMProvider.GEMINI;
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
}
