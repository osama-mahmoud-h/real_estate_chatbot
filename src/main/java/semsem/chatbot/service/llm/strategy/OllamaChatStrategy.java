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
 * Ollama Chat LLM Strategy (Local).
 */
@Slf4j
@Component
public class OllamaChatStrategy extends BaseChatLLMStrategy {

    @Value("${llm.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${llm.ollama.chat.model:llama3}")
    private String model;

    // TODO: Inject Spring AI OllamaChatModel or WebClient

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        // TODO: Implement using Ollama API
        log.debug("Generating with Ollama model: {}", model);
        return "";
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        // TODO: Implement streaming using Ollama API
        log.debug("Streaming with Ollama model: {}", model);
        return Flux.empty();
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        // TODO: Implement chat using Ollama API
        log.debug("Chat with Ollama model: {}", model);
        return "";
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        // TODO: Implement streaming chat using Ollama API
        log.debug("Chat streaming with Ollama model: {}", model);
        return Flux.empty();
    }

    @Override
    protected String doGenerate(LLMRequest request) {
        // TODO: Implement
        return "";
    }

    @Override
    public LLMProvider getProvider() {
        return LLMProvider.OLLAMA;
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        // TODO: Check if Ollama server is reachable
        return baseUrl != null && !baseUrl.isBlank();
    }
}
