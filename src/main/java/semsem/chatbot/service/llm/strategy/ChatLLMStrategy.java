package semsem.chatbot.service.llm.strategy;

import reactor.core.publisher.Flux;
import semsem.chatbot.model.enums.LLMProvider;
import semsem.chatbot.service.llm.dto.LLMRequest;
import semsem.chatbot.service.llm.dto.LLMResponse;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for Chat LLM providers.
 * Supports both cloud (Gemini, Cohere) and local (Ollama) providers.
 */
public interface ChatLLMStrategy {

    /**
     * Generate a response from a simple prompt.
     */
    String generate(String prompt);

    /**
     * Generate a response with custom options.
     */
    String generate(String prompt, Map<String, Object> options);

    /**
     * Generate a response with full request/response metadata.
     */
    LLMResponse generateWithMetadata(LLMRequest request);

    /**
     * Stream a response from a simple prompt.
     */
    Flux<String> generateStream(String prompt);

    /**
     * Stream a response with custom options.
     */
    Flux<String> generateStream(String prompt, Map<String, Object> options);

    /**
     * Chat with message history.
     */
    String chat(List<Map<String, String>> messages);

    /**
     * Chat with message history and options.
     */
    String chat(List<Map<String, String>> messages, Map<String, Object> options);

    /**
     * Stream chat with message history.
     */
    Flux<String> chatStream(List<Map<String, String>> messages);

    /**
     * Get the provider type.
     */
    LLMProvider getProvider();

    /**
     * Get the model name.
     */
    String getModelName();

    /**
     * Check if this provider is available/configured.
     */
    boolean isAvailable();
}
