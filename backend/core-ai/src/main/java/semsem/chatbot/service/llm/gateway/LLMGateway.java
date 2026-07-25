package semsem.chatbot.service.llm.gateway;

import org.springframework.ai.chat.prompt.Prompt;

/**
 * Synchronous text generation over an abstract LLM instance.
 *
 * <p>Callers assemble a role-tagged {@link Prompt} (Spring AI's native
 * {@code SystemMessage} / {@code UserMessage} / {@code AssistantMessage}) and invoke it here.
 * Prompt <em>assembly</em> (which messages, in which roles) is kept separate from
 * <em>invocation</em> (this gateway), so the two evolve independently.
 *
 * <p>This is the smallest useful capability. Other modalities are modeled as
 * <em>separate</em> interfaces so no client or implementation is forced to depend on
 * behaviour it does not use (Interface Segregation), mirroring Spring AI's own split of
 * {@code ChatModel} vs {@code StreamingChatModel}:
 * <ul>
 *   <li>{@link StreamingLLMGateway} — token/chunk streaming</li>
 *   <li>{@link StructuredLLMGateway} — parse the reply into a typed object</li>
 * </ul>
 * An implementation advertises a capability by implementing that interface; there are no
 * optional methods that throw at runtime.
 */
public interface LLMGateway {

    /**
     * Synchronous text generation. Returns the model's reply as plain text
     * (never {@code null}; empty string on an empty/failed generation).
     */
    String invoke(Prompt prompt);
}