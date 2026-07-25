package semsem.chatbot.config.llm;

import java.util.Optional;

/**
 * Interface for provider configurations.
 * Provides access to provider credentials and models.
 */
public interface IProviderConfig {

    /**
     * Get the API key for cloud providers (null for local providers).
     */
    String getApiKey();

    /**
     * Get the base URL for API calls.
     */
    String getBaseUrl();

    /**
     * Get a model configuration by name.
     * Returns AbstractModelConfig which can be ChatModelConfig or EmbeddingModelConfig.
     */
    Optional<AbstractModelConfig> getModel(String modelName);

    /**
     * Check if the provider is available and properly configured.
     */
    boolean isAvailable();

    /**
     * Check if this is a cloud provider (has API key).
     */
    boolean isCloud();

    /**
     * Check if this is a local provider (no API key).
     */
    boolean isLocal();
}
