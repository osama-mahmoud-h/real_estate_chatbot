package semsem.chatbot.llm.gateway;

import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

/**
 * Streaming text generation capability — a separate interface so only clients and
 * implementations that actually stream depend on it (Interface Segregation).
 */
public interface StreamingLLMGateway {

    /**
     * Stream the model's reply as a sequence of text chunks using the default configured model.
     */
    Flux<String> stream(Prompt prompt);

    /**
     * Stream the model's reply as a sequence of text chunks using the model assigned to
     * {@code (flowName, stepName)} in the database (falling back to the {@code .env} default).
     */
    default Flux<String> stream(String flowName, String stepName, Prompt prompt) {
        return stream(prompt);
    }
}
