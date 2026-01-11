package semsem.chatbot.service.embedding;

import java.util.List;

/**
 * Interface for generating text embeddings.
 */
public interface EmbeddingService {

    float[] embed(String text);

    List<float[]> embed(List<String> texts);

    int getDimensions();

    String getModelName();
}
