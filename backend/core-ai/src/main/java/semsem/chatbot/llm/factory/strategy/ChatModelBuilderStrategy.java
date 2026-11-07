package semsem.chatbot.llm.factory.strategy;

import org.springframework.ai.chat.model.ChatModel;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;

/**
 * Strategy interface for building Spring AI {@link ChatModel} instances from a
 * database-backed {@link ResolvedChatModel}.
 *
 * <p>Ordered strategies (via {@code @Order}) allow specific SDK implementations (e.g. Google Gemini)
 * to take precedence over general protocol strategies (e.g. Cloud/Local OpenAI-compatible endpoints
 * such as OpenRouter, Cohere, Ollama, vLLM, or LM Studio).
 */
public interface ChatModelBuilderStrategy {

    /**
     * Returns {@code true} if this strategy can build a {@link ChatModel} for the given configuration.
     */
    boolean supports(ResolvedChatModel resolved);

    /**
     * Constructs a configured Spring AI {@link ChatModel} for the given resolved model configuration.
     */
    ChatModel build(ResolvedChatModel resolved);
}
