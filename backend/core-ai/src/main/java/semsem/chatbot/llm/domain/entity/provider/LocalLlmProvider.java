package semsem.chatbot.llm.domain.entity.provider;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Self-hosted / local LLM provider (e.g. Ollama, vLLM, LM Studio).
 * Requires a base URL and needs no external API key.
 */
@Entity
@DiscriminatorValue("LOCAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalLlmProvider extends LlmProvider {

    private static final String LOCAL_NO_KEY_PLACEHOLDER = "local-no-key";

    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds;

    @Column(name = "keep_alive", length = 30)
    private String keepAlive;

    private LocalLlmProvider(String providerKey, String displayName, String baseUrl,
                             Integer timeoutSeconds, String keepAlive) {
        super(providerKey, displayName, baseUrl);
        this.timeoutSeconds = timeoutSeconds;
        this.keepAlive = keepAlive;
    }

    public static LocalLlmProvider create(String providerKey, String displayName, String baseUrl,
                                          Integer timeoutSeconds, String keepAlive) {
        return new LocalLlmProvider(providerKey, displayName, baseUrl, timeoutSeconds, keepAlive);
    }

    @Override
    public String getApiKey() {
        return null;
    }

    @Override
    public String requireApiKey() {
        return LOCAL_NO_KEY_PLACEHOLDER;
    }

    @Override
    public Map<String, Object> mergeProviderOptions(Map<String, Object> modelExtraConfig) {
        Map<String, Object> merged = new HashMap<>(super.mergeProviderOptions(modelExtraConfig));
        if (keepAlive != null && !keepAlive.isBlank()) {
            merged.putIfAbsent("keep_alive", keepAlive);
        }
        return Collections.unmodifiableMap(merged);
    }

    @Override
    public boolean isCloud() {
        return false;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public boolean isAvailable() {
        return isEnabled() && getBaseUrl() != null && !getBaseUrl().isBlank();
    }

    @Override
    public int getEffectiveTimeoutSeconds() {
        if (timeoutSeconds != null && timeoutSeconds > 0) {
            return timeoutSeconds;
        }
        return super.getEffectiveTimeoutSeconds();
    }
}
