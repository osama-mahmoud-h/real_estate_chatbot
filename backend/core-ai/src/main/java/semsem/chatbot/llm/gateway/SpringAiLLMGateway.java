package semsem.chatbot.llm.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;
import semsem.chatbot.llm.factory.DynamicChatModelFactory;
import semsem.chatbot.llm.service.LlmModelResolver;

import java.util.Optional;

/**
 * Fail-safe Spring AI-backed gateway that dynamically resolves the target {@link ChatModel}
 * per workflow step from the database (via {@link LlmModelResolver} and {@link DynamicChatModelFactory}),
 * falling back to the {@code .env} default model key when a step has no override.
 *
 * <p>Implements synchronous text ({@link LLMGateway}), structured output ({@link StructuredLLMGateway}),
 * and reactive token streaming ({@link StreamingLLMGateway}) with multi-level error handling and
 * production-ready logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAiLLMGateway implements LLMGateway, StructuredLLMGateway, StreamingLLMGateway {

    private final DynamicChatModelFactory chatModelFactory;
    private final LlmModelResolver modelResolver;
    private final ObjectMapper objectMapper;

    @Override
    public String invoke(Prompt prompt) {
        return invoke(null, null, prompt);
    }

    @Override
    public String invoke(String flowName, String stepName, Prompt prompt) {
        ResolvedChatModel resolved = null;
        long startTime = System.currentTimeMillis();
        try {
            resolved = resolveModelConfig(flowName, stepName);
            ChatModel targetModel = chatModelFactory.getOrCreate(resolved);

            if (log.isDebugEnabled() && resolved != null) {
                log.debug("Invoking LLM [flow={}, step={}, key={}, provider={}, model={}, messages={}]",
                        flowName, stepName, resolved.modelKey(), resolved.providerKey(),
                        resolved.modelName(), prompt != null && prompt.getInstructions() != null ? prompt.getInstructions().size() : 0);
            }

            ChatResponse response = targetModel.call(prompt);
            long latencyMs = System.currentTimeMillis() - startTime;
            logTokenUsage(response, flowName, stepName, latencyMs);

            String text = extractText(response);
            if (text.isBlank()) {
                log.warn("LLM returned empty response for [flow={}, step={}, model={}] after {}ms",
                        flowName, stepName, resolved != null ? resolved.modelKey() : "default", latencyMs);
            } else {
                log.debug("LLM call completed [flow={}, step={}, latency={}ms, responseLength={}]",
                        flowName, stepName, latencyMs, text.length());
            }
            return text;
        } catch (Exception ex) {
            logAiError("SYNC_INVOKE", flowName, stepName, resolved, ex);
            return "";
        }
    }

    @Override
    public <T> T invokeStructured(Prompt prompt, Class<T> type) {
        return invokeStructured(null, null, prompt, type);
    }

    @Override
    public <T> T invokeStructured(String flowName, String stepName, Prompt prompt, Class<T> type) {
        log.debug("Starting structured LLM invocation [flow={}, step={}, targetType={}]",
                flowName, stepName, type != null ? type.getSimpleName() : "null");
        String rawReply = invoke(flowName, stepName, prompt);
        return parseStructured(rawReply, type, flowName, stepName);
    }

    @Override
    public Flux<String> stream(Prompt prompt) {
        return stream(null, null, prompt);
    }

    @Override
    public Flux<String> stream(String flowName, String stepName, Prompt prompt) {
        ResolvedChatModel resolved = null;
        try {
            resolved = resolveModelConfig(flowName, stepName);
            ResolvedChatModel finalResolved = resolved;
            ChatModel targetModel = chatModelFactory.getOrCreate(resolved);

            if (log.isDebugEnabled() && resolved != null) {
                log.debug("Starting LLM stream [flow={}, step={}, key={}, provider={}, model={}]",
                        flowName, stepName, resolved.modelKey(), resolved.providerKey(), resolved.modelName());
            }

            return targetModel.stream(prompt)
                    .map(this::extractText)
                    .filter(chunk -> !chunk.isEmpty())
                    .doOnComplete(() -> log.debug("Completed LLM stream [flow={}, step={}]", flowName, stepName))
                    .onErrorResume(ex -> {
                        logAiError("STREAM_INVOKE", flowName, stepName, finalResolved, ex);
                        return Flux.empty();
                    });
        } catch (Exception ex) {
            logAiError("STREAM_SETUP", flowName, stepName, resolved, ex);
            return Flux.empty();
        }
    }

    /**
     * Multi-level model resolution: attempts step-specific config first, then falls back
     * to default chat model if step lookup fails.
     */
    private ResolvedChatModel resolveModelConfig(String flowName, String stepName) {
        if (flowName != null && stepName != null) {
            try {
                return modelResolver.resolveForStep(flowName, stepName);
            } catch (Exception stepEx) {
                log.warn("Step model resolution failed for [flow={}, step={}]: {}. Attempting default chat model fallback.",
                        flowName, stepName, stepEx.getMessage());
                return modelResolver.resolveDefaultChatModel();
            }
        }
        return modelResolver.resolveDefaultChatModel();
    }

    private void logAiError(String operation, String flowName, String stepName,
                            ResolvedChatModel resolved, Throwable ex) {
        String stepDesc = (flowName != null && stepName != null)
                ? flowName + ":" + stepName
                : "default";
        String modelDesc = (resolved != null)
                ? String.format("key=%s, provider=%s, model=%s, baseUrl=%s",
                        resolved.modelKey(), resolved.providerKey(), resolved.modelName(), resolved.baseUrl())
                : "unresolved (check llm_providers / llm_models table and AI_DEFAULT_CHAT_MODEL_KEY in .env)";

        log.error("\n================================================================================\n" +
                  "[AI_CONNECTION_ERROR] Operation={} | Step={} | Model=[{}]\n" +
                  "Reason: {}\n" +
                  "================================================================================",
                operation, stepDesc, modelDesc, ex.getMessage(), ex);
    }

    private String extractText(ChatResponse response) {
        try {
            return Optional.ofNullable(response)
                    .map(ChatResponse::getResult)
                    .map(result -> result.getOutput())
                    .map(output -> output.getText())
                    .orElse("");
        } catch (Exception ex) {
            log.warn("Failed to extract text from ChatResponse: {}", ex.getMessage());
            return "";
        }
    }

    private void logTokenUsage(ChatResponse response, String flowName, String stepName, long latencyMs) {
        try {
            Optional.ofNullable(response)
                    .map(ChatResponse::getMetadata)
                    .map(ChatResponseMetadata::getUsage)
                    .ifPresent(usage -> log.info("LLM tokens [{}] [step={}:{}] prompt={} completion={} total={} latency={}ms",
                            response.getMetadata().getModel(),
                            flowName != null ? flowName : "default",
                            stepName != null ? stepName : "default",
                            usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), latencyMs));
        } catch (Exception ex) {
            log.warn("Could not log token usage metadata: {}", ex.getMessage(), ex);
        }
    }

    private <T> T parseStructured(String text, Class<T> type, String flowName, String stepName) {
        if (text == null || text.isBlank()) {
            log.error("LLM returned empty content for structured target {} [flow={}, step={}]",
                    type != null ? type.getSimpleName() : "unknown", flowName, stepName);
            throw new IllegalStateException("LLM returned no content to parse into " + (type != null ? type.getSimpleName() : "unknown"));
        }

        log.info("Invoked LLM, for stage : {} , got response: {}", type.getSimpleName(), text.length());
        String jsonCandidate = extractJsonObject(text);
        log.debug("Extracted JSON candidate for {} [flow={}, step={}]: {}",
                type.getSimpleName(), flowName, stepName, jsonCandidate);

        try {
            return new BeanOutputConverter<>(type, objectMapper).convert(jsonCandidate);
        } catch (Exception ex) {
            log.error("Failed to parse LLM JSON output into {} [flow={}, step={}]: {}. Raw JSON candidate: {}",
                    type.getSimpleName(), flowName, stepName, ex.getMessage(), jsonCandidate, ex);
            throw new IllegalStateException("Failed to parse structured LLM output into " + type.getSimpleName() + ": " + ex.getMessage(), ex);
        }
    }

    /**
     * Isolates the JSON object from a raw model reply, tolerating markdown fences
     * (including an unbalanced ```json opener) and surrounding prose.
     */
    private String extractJsonObject(String text) {
        try {
            int start = text.indexOf('{');
            int end = text.lastIndexOf('}');
            return (start >= 0 && end > start) ? text.substring(start, end + 1) : text.strip();
        } catch (Exception ex) {
            log.warn("Error isolating JSON object from LLM text, returning stripped text: {}", ex.getMessage());
            return text != null ? text.strip() : "";
        }
    }
}
