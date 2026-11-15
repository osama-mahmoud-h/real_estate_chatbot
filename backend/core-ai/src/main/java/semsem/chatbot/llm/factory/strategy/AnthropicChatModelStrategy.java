package semsem.chatbot.llm.factory.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;

import java.util.Map;

/**
 * Strategy that builds a native {@link AnthropicChatModel} for Anthropic Claude models.
 */
@Slf4j
@Component
@Order(10)
public class AnthropicChatModelStrategy implements ChatModelBuilderStrategy {

    private static final String ANTHROPIC_PROVIDER_KEY = "anthropic";

    @Override
    public boolean supports(ResolvedChatModel resolved) {
        return resolved != null && ANTHROPIC_PROVIDER_KEY.equalsIgnoreCase(resolved.providerKey());
    }

    @Override
    public ChatModel build(ResolvedChatModel resolved) {
        try {
            int timeoutSec = resolved.timeoutSeconds();
            log.debug("Building native AnthropicChatModel for model='{}', baseUrl='{}', maxTokens={}, temp={}, topP={}, timeout={}s",
                    resolved.modelName(), resolved.baseUrl(), resolved.maxTokens(), resolved.temperature(), resolved.topP(), timeoutSec);

            AnthropicApi.Builder apiBuilder = AnthropicApi.builder()
                    .apiKey(resolved.requireApiKey())
                    .restClientBuilder(OpenAiChatModelStrategy.createRestClientBuilder(timeoutSec))
                    .webClientBuilder(OpenAiChatModelStrategy.createWebClientBuilder(timeoutSec));

            if (resolved.baseUrl() != null && !resolved.baseUrl().isBlank()) {
                apiBuilder.baseUrl(OpenAiChatModelStrategy.normalizeBaseUrl(resolved.baseUrl()));
            }

            AnthropicChatOptions.Builder optionsBuilder = AnthropicChatOptions.builder()
                    .model(resolved.modelName())
                    .maxTokens(resolved.maxTokens())
                    .temperature(resolved.temperature())
                    .topP(resolved.topP());

            try {
                Map<String, Object> extra = resolved.effectiveExtraBody();
                Object topK = extra != null ? extra.get("top_k") : null;
                if (topK instanceof Number num) {
                    optionsBuilder.topK(num.intValue());
                    log.debug("Applied top_k={} to AnthropicChatOptions for model='{}'", num.intValue(), resolved.modelName());
                }
            } catch (Exception extraEx) {
                log.warn("Failed to apply optional extraConfig to AnthropicChatOptions for model '{}': {}",
                        resolved.modelName(), extraEx.getMessage());
            }

            return AnthropicChatModel.builder()
                    .anthropicApi(apiBuilder.build())
                    .defaultOptions(optionsBuilder.build())
                    .build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("AnthropicChatModel configuration error for model='{}': {}", resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected failure building AnthropicChatModel for model='{}': {}",
                    resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to build AnthropicChatModel: " + ex.getMessage(), ex);
        }
    }
}
