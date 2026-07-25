package semsem.chatbot.service.llm.gateway;

import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

/**
 * Streaming text generation capability — a separate interface so only clients and
 * implementations that actually stream depend on it (Interface Segregation).
 *
 * <p>Extension seam: back this with Spring AI's {@code ChatModel.stream(Prompt)}.
 * Not implemented yet.
 */
public interface StreamingLLMGateway {

    /**
     * Stream the model's reply as a sequence of text chunks.
     */
    Flux<String> stream(Prompt prompt);
}