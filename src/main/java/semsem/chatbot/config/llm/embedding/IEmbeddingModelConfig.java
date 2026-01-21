package semsem.chatbot.config.llm.embedding;

import semsem.chatbot.config.llm.IModelConfig;

/**
 * Interface for embedding model configurations.
 * Extends IModelConfig with embedding-specific properties.
 */
public interface IEmbeddingModelConfig extends IModelConfig {

    /**
     * Get the vector dimensions.
     */
    int getDimensions();

    /**
     * Get the maximum input tokens.
     */
    int getMaxInputTokens();

    /**
     * Check if the model supports batch embedding.
     */
    boolean supportsBatch();

    /**
     * Check if the model supports multilingual input.
     */
    boolean supportsMultilingual();
}