package semsem.chatbot.service.llm.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import semsem.chatbot.config.AIProperties;
import semsem.chatbot.config.ai.ModelConfig;
import semsem.chatbot.config.ai.ProviderConfig;
import semsem.chatbot.model.enums.LLMProvider;
import semsem.chatbot.service.llm.dto.LLMRequest;

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

    private final AIProperties aiProperties;

    private ProviderConfig getProviderConfig() {
        return aiProperties.getProviderOrThrow("gemini");
    }

    private ModelConfig getModelConfig() {
        String modelName = aiProperties.getChat().getModel();
        return getProviderConfig().getModel(modelName).orElse(null);
    }

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        var config = getProviderConfig();
        var model = getModelConfig();
        log.debug("Generating with Gemini model: {}, baseUrl: {}",
                aiProperties.getChat().getModel(), config.getBaseUrl());
        if (model != null && model.supportsVision()) {
            log.debug("Model supports vision capability");
        }
        // TODO: Implement using Google Gemini API
        return "";
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        log.debug("Streaming with Gemini model: {}", aiProperties.getChat().getModel());
        // TODO: Implement streaming
        return Flux.empty();
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        log.debug("Chat with Gemini model: {}", aiProperties.getChat().getModel());
        // TODO: Implement chat
        return "";
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        log.debug("Chat streaming with Gemini model: {}", aiProperties.getChat().getModel());
        // TODO: Implement streaming chat
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
        return aiProperties.getChat().getModel();
    }

    @Override
    public boolean isAvailable() {
        return aiProperties.getProvider("gemini")
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }
}