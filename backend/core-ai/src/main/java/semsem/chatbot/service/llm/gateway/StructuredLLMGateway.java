package semsem.chatbot.service.llm.gateway;

import org.springframework.ai.chat.prompt.Prompt;

/**
 * Structured (typed) generation capability — a separate interface so only clients and
 * implementations that need typed output depend on it (Interface Segregation).
 *
 * <p>Extension seam: back this with Spring AI's {@code BeanOutputConverter} /
 * {@code ChatClient.entity(...)}. Not implemented yet.
 */
public interface StructuredLLMGateway {

    /**
     * Generate and parse the model reply into an instance of {@code type}.
     */
    <T> T invokeStructured(Prompt prompt, Class<T> type);
}