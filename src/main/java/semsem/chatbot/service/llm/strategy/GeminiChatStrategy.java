package semsem.chatbot.service.llm.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import semsem.chatbot.config.LLMProperties;
import semsem.chatbot.config.llm.chat.ChatModelConfig;
import semsem.chatbot.config.llm.ProviderConfig;
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

    private final LLMProperties llmProperties;

    private ProviderConfig getProviderConfig() {
        return llmProperties.getProviderOrThrow("gemini");
    }

    private ChatModelConfig getModelConfig() {
        String modelName = llmProperties.getChat().getModel();
        return getProviderConfig().getChatModel(modelName).orElse(null);
    }

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        var config = getProviderConfig();
        var model = getModelConfig();
        log.debug("Generating with Gemini model: {}, baseUrl: {}",
                llmProperties.getChat().getModel(), config.getBaseUrl());
        if (model != null && model.supportsVision()) {
            log.debug("Model supports vision capability");
        }
        // TODO: Implement using Google Gemini API
        return "";
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        log.debug("Streaming with Gemini model: {}", llmProperties.getChat().getModel());
        // TODO: Implement streaming
        return Flux.empty();
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        log.debug("Chat with Gemini model: {}", llmProperties.getChat().getModel());
        // TODO: Implement chat
        return "";
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        log.debug("Chat streaming with Gemini model: {}", llmProperties.getChat().getModel());
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
        return llmProperties.getChat().getModel();
    }

    @Override
    public boolean isAvailable() {
        return llmProperties.getProvider("gemini")
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }
}