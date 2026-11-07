package semsem.chatbot.llm.gateway;

import org.springframework.ai.chat.prompt.Prompt;

/**
 * Structured (typed) generation capability — a separate interface so only clients and
 * implementations that need typed output depend on it (Interface Segregation).
 */
public interface StructuredLLMGateway {

    /**
     * Generate and parse the model reply into an instance of {@code type} using the default model.
     */
    <T> T invokeStructured(Prompt prompt, Class<T> type);

    /**
     * Generate and parse the model reply into an instance of {@code type} using the model
     * assigned to {@code (flowName, stepName)} in the database (falling back to the {@code .env} default).
     */
    default <T> T invokeStructured(String flowName, String stepName, Prompt prompt, Class<T> type) {
        return invokeStructured(prompt, type);
    }
}
