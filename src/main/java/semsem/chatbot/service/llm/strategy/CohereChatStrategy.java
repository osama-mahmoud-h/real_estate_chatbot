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
 * Cohere Chat LLM Strategy (Cloud).
 */
@Slf4j
@Component
public class CohereChatStrategy extends BaseChatLLMStrategy {

    @Value("${llm.cohere.api-key:}")
    private String apiKey;

    @Value("${llm.cohere.model:command-r-plus}")
    private String model;

    @Value("${llm.cohere.base-url:https://api.cohere.ai}")
    private String baseUrl;

    // TODO: Inject WebClient for Cohere API

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        // TODO: Implement using Cohere API
        log.debug("Generating with Cohere model: {}", model);
        return "";
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        // TODO: Implement streaming using Cohere API
        log.debug("Streaming with Cohere model: {}", model);
        return Flux.empty();
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        // TODO: Implement chat using Cohere API
        log.debug("Chat with Cohere model: {}", model);
        return "";
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        // TODO: Implement streaming chat using Cohere API
        log.debug("Chat streaming with Cohere model: {}", model);
        return Flux.empty();
    }

    @Override
    protected String doGenerate(LLMRequest request) {
        // TODO: Implement
        return "";
    }

    @Override
    public LLMProvider getProvider() {
        return LLMProvider.COHERE;
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
