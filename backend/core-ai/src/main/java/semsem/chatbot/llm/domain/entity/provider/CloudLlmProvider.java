package semsem.chatbot.llm.domain.entity.provider;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Cloud-hosted LLM provider (e.g. OpenRouter, Google Gemini, Cohere, Anthropic).
 * Reads the provider API key directly from the {@code llm_providers.api_key} database column.
 */
@Entity
@DiscriminatorValue("CLOUD")
@Getter
@ToString(callSuper = true, exclude = "apiKey")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CloudLlmProvider extends LlmProvider {

    @Column(name = "api_key", length = 255)
    private String apiKey;

    private CloudLlmProvider(String providerKey, String displayName, String baseUrl, String apiKey) {
        super(providerKey, displayName, baseUrl);
        this.apiKey = apiKey;
    }

    public static CloudLlmProvider create(String providerKey, String displayName, String baseUrl, String apiKey) {
        return new CloudLlmProvider(providerKey, displayName, baseUrl, apiKey);
    }

    public void changeApiKey(String apiKey) {
        this.apiKey = apiKey;
        touch();
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String requireApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing API key for cloud provider '" + getProviderKey() + "' (check llm_providers.api_key in database)"
            );
        }
        return apiKey.strip();
    }

    @Override
    public boolean isCloud() {
        return true;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isAvailable() {
        return isEnabled()
                && getBaseUrl() != null && !getBaseUrl().isBlank()
                && apiKey != null && !apiKey.isBlank();
    }
}
