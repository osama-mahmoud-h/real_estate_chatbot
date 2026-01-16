package semsem.chatbot.service.embedding;

import java.util.List;

/**
 * Interface for generating text embeddings.
 * Delegates to EmbeddingStrategy implementations.
 */
public interface EmbeddingService {

    float[] embed(String text);

    List<float[]> embedBatch(List<String> texts);

    int getDimensions();

    String getModelName();
}
