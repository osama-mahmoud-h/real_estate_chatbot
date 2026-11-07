package semsem.chatbot.llm.factory.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;
import semsem.chatbot.llm.domain.entity.provider.LocalLlmProvider;

import java.util.Map;

/**
 * Strategy that builds a native {@link OllamaChatModel} for local Ollama instances,
 * mapping {@code keep_alive}, {@code contextWindow} ({@code numCtx}), and generation options natively.
 */
@Slf4j
@Component
@Order(10)
public class OllamaChatModelStrategy implements ChatModelBuilderStrategy {

    private static final String OLLAMA_PROVIDER_KEY = "ollama";

    @Override
    public boolean supports(ResolvedChatModel resolved) {
        return resolved != null && OLLAMA_PROVIDER_KEY.equalsIgnoreCase(resolved.providerKey());
    }

    @Override
    public ChatModel build(ResolvedChatModel resolved) {
        try {
            String normalizedUrl = OpenAiChatModelStrategy.normalizeBaseUrl(resolved.baseUrl());
            int timeoutSec = resolved.timeoutSeconds();
            log.debug("Building native OllamaChatModel for model='{}', baseUrl='{}', numPredict={}, temp={}, topP={}, timeout={}s",
                    resolved.modelName(), normalizedUrl, resolved.maxTokens(), resolved.temperature(), resolved.topP(), timeoutSec);

            OllamaApi ollamaApi = OllamaApi.builder()
                    .baseUrl(normalizedUrl)
                    .restClientBuilder(OpenAiChatModelStrategy.createRestClientBuilder(timeoutSec))
                    .webClientBuilder(OpenAiChatModelStrategy.createWebClientBuilder(timeoutSec))
                    .build();

            OllamaChatOptions.Builder optionsBuilder = OllamaChatOptions.builder()
                    .model(resolved.modelName())
                    .numPredict(resolved.maxTokens())
                    .temperature(resolved.temperature())
                    .topP(resolved.topP());

            if (resolved.model() != null && resolved.model().getContextWindow() > 0) {
                optionsBuilder.numCtx(resolved.model().getContextWindow());
            }

            if (resolved.provider() instanceof LocalLlmProvider localProvider
                    && localProvider.getKeepAlive() != null
                    && !localProvider.getKeepAlive().isBlank()) {
                optionsBuilder.keepAlive(localProvider.getKeepAlive());
                log.debug("Configured Ollama keep_alive='{}' for model='{}'",
                        localProvider.getKeepAlive(), resolved.modelName());
            }

            try {
                Map<String, Object> extra = resolved.effectiveExtraBody();
                Object topK = extra != null ? extra.get("top_k") : null;
                if (topK instanceof Number num) {
                    optionsBuilder.topK(num.intValue());
                }
            } catch (Exception extraEx) {
                log.warn("Failed to apply optional extraConfig to OllamaChatOptions for model '{}': {}",
                        resolved.modelName(), extraEx.getMessage());
            }

            return OllamaChatModel.builder()
                    .ollamaApi(ollamaApi)
                    .defaultOptions(optionsBuilder.build())
                    .build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("OllamaChatModel configuration error for model='{}': {}", resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected failure building OllamaChatModel for model='{}': {}",
                    resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to build OllamaChatModel: " + ex.getMessage(), ex);
        }
    }
}
