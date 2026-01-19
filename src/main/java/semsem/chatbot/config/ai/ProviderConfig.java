package semsem.chatbot.config.ai;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Concrete provider configuration.
 * Used for all providers (Gemini, Cohere, Ollama).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProviderConfig extends AbstractProviderConfig {

    @Override
    public boolean isAvailable() {
        // Cloud providers need API key, local providers need base URL
        if (isCloud()) {
            return apiKey != null && !apiKey.isBlank();
        }
        return baseUrl != null && !baseUrl.isBlank();
    }
}