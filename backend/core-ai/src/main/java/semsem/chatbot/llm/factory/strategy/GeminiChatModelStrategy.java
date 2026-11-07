package semsem.chatbot.llm.factory.strategy;

import com.google.genai.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;

import java.util.Map;

/**
 * Strategy that builds a native {@link GoogleGenAiChatModel} for Google Gemini cloud models.
 */
@Slf4j
@Component
@Order(10)
public class GeminiChatModelStrategy implements ChatModelBuilderStrategy {

    private static final String GEMINI_PROVIDER_KEY = "gemini";

    @Override
    public boolean supports(ResolvedChatModel resolved) {
        return resolved != null && GEMINI_PROVIDER_KEY.equalsIgnoreCase(resolved.providerKey());
    }

    @Override
    public ChatModel build(ResolvedChatModel resolved) {
        try {
            log.debug("Building native GoogleGenAiChatModel for model='{}', maxOutputTokens={}, temp={}, topP={}",
                    resolved.modelName(), resolved.maxTokens(), resolved.temperature(), resolved.topP());

            Client client = Client.builder()
                    .apiKey(resolved.requireApiKey())
                    .build();

            GoogleGenAiChatOptions.Builder optionsBuilder = GoogleGenAiChatOptions.builder()
                    .model(resolved.modelName())
                    .temperature(resolved.temperature())
                    .maxOutputTokens(resolved.maxTokens())
                    .topP(resolved.topP());

            try {
                Map<String, Object> extra = resolved.effectiveExtraBody();
                Object topK = extra != null ? extra.get("top_k") : null;
                if (topK instanceof Number num) {
                    optionsBuilder.topK(num.intValue());
                    log.debug("Applied top_k={} to GoogleGenAiChatOptions for model='{}'", num.intValue(), resolved.modelName());
                }
            } catch (Exception extraEx) {
                log.warn("Failed to apply optional extraConfig to GoogleGenAiChatOptions for model '{}': {}",
                        resolved.modelName(), extraEx.getMessage());
            }

            return GoogleGenAiChatModel.builder()
                    .genAiClient(client)
                    .defaultOptions(optionsBuilder.build())
                    .build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("GeminiChatModel configuration error for model='{}': {}", resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected failure building GoogleGenAiChatModel for model='{}': {}",
                    resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to build GoogleGenAiChatModel: " + ex.getMessage(), ex);
        }
    }
}
