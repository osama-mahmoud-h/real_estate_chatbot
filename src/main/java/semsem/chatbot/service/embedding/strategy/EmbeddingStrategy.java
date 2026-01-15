package semsem.chatbot.service.embedding.strategy;

import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.List;

/**
 * Strategy interface for Embedding providers.
 * Supports both cloud (Gemini, Cohere) and local (Ollama) providers.
 */
public interface EmbeddingStrategy {

    /**
     * Generate embedding for a single text.
     */
    float[] embed(String text);

    /**
     * Generate embeddings for multiple texts (batch).
     */
    List<float[]> embedBatch(List<String> texts);

    /**
     * Get the embedding dimensions for this model.
     */
    int getDimensions();

    /**
     * Get the provider type.
     */
    EmbeddingProvider getProvider();

    /**
     * Get the model name.
     */
    String getModelName();

    /**
     * Check if this provider is available/configured.
     */
    boolean isAvailable();
}
