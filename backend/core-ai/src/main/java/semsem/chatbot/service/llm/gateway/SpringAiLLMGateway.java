package semsem.chatbot.service.llm.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Spring AI-backed gateway. The "abstract LLM instance" is Spring AI's {@link ChatModel},
 * auto-configured from {@code spring.ai.google.genai.*}.
 *
 * <p>Advertises capabilities by implementing the corresponding interface — synchronous text
 * ({@link LLMGateway}) and structured output ({@link StructuredLLMGateway}). Streaming
 * ({@link StreamingLLMGateway}) is intentionally not implemented yet.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiLLMGateway implements LLMGateway, StructuredLLMGateway {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Override
    public String invoke(Prompt prompt) {
        try {
            ChatResponse response = chatModel.call(prompt);
            logTokenUsage(response);
            return Optional.ofNullable(response)
                    .map(ChatResponse::getResult)
                    .map(result -> result.getOutput())
                    .map(output -> output.getText())
                    .orElse("");
        } catch (Exception e) {
            log.error("LLM invocation failed: {}", e.getMessage(), e);
            return "";
        }
    }

    private void logTokenUsage(ChatResponse response) {
        Optional.ofNullable(response)
                .map(ChatResponse::getMetadata)
                .map(ChatResponseMetadata::getUsage)
                .ifPresent(usage -> log.info("LLM tokens [{}] prompt={} completion={} total={}",
                        response.getMetadata().getModel(),
                        usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens()));
    }

    /**
     * Generates text, then parses it into {@code type} using Spring AI's
     * {@link BeanOutputConverter} (strips ```json fences, deserializes via the app's
     * {@link ObjectMapper}). The prompt is expected to already instruct the model to emit JSON;
     * no auto-generated format instructions are appended, preserving the callers' hand-tuned
     * output schemas.
     *
     * @throws IllegalStateException if the model returns no content to parse
     */
    @Override
    public <T> T invokeStructured(Prompt prompt, Class<T> type) {
        String text = invoke(prompt);
        if (text.isBlank()) {
            throw new IllegalStateException("LLM returned no content to parse into " + type.getSimpleName());
        }
        //TODO: convert to debug level
        log.info("Invoked LLM, for stage : {} , got response: {}", type.getSimpleName(), text);
        return new BeanOutputConverter<>(type, objectMapper).convert(extractJsonObject(text));
    }

    /**
     * Isolates the JSON object from a raw model reply, tolerating markdown fences
     * (including an unbalanced ```json opener) and surrounding prose.
     */
    private String extractJsonObject(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        return (start >= 0 && end > start) ? text.substring(start, end + 1) : text.strip();
    }
}