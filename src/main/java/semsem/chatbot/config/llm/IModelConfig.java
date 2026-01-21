package semsem.chatbot.config.llm;

/**
 * Base interface for all model configurations.
 * Provides capability checking methods for type-safe model validation.
 */
public interface IModelConfig {

    /**
     * Get the model type identifier (e.g., "chat", "embedding").
     */
    String getType();

    /**
     * Check if the model has a specific capability.
     */
    boolean hasCapability(ModelCapability capability);

    /**
     * Check if the model has all the specified capabilities.
     */
    boolean hasAllCapabilities(ModelCapability... required);

    /**
     * Check if the model has any of the specified capabilities.
     */
    boolean hasAnyCapability(ModelCapability... required);
}