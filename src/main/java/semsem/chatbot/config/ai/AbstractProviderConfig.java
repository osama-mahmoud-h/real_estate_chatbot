package semsem.chatbot.config.ai;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract base for provider configurations.
 * Contains credentials and models with capabilities.
 */
@Data
public abstract class AbstractProviderConfig {

    /** API key (null for local providers like Ollama) */
    protected String apiKey;

    /** Base URL for API calls */
    protected String baseUrl;

    /** Models available from this provider */
    protected Map<String, ModelConfig> models = new HashMap<>();

    // =========================================================================
    // MODEL ACCESS
    // =========================================================================

    public Optional<ModelConfig> getModel(String modelName) {
        return Optional.ofNullable(models.get(modelName));
    }

    public ModelConfig getModelOrThrow(String modelName) {
        return getModel(modelName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Model not found: " + modelName));
    }

    public boolean hasModel(String modelName) {
        return models.containsKey(modelName);
    }

    // =========================================================================
    // AVAILABILITY
    // =========================================================================

    public abstract boolean isAvailable();

    public boolean isCloud() {
        return apiKey != null && !apiKey.isBlank();
    }

    public boolean isLocal() {
        return !isCloud();
    }
}