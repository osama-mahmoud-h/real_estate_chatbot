package semsem.chatbot.service.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Embedding service using Spring AI EmbeddingModel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingModelService implements EmbeddingService {

    // TODO: Inject Spring AI EmbeddingModel
    // private final EmbeddingModel embeddingModel;

    @Override
    public float[] embed(String text) {
        // TODO: Implement using Spring AI
        return new float[0];
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        // TODO: Implement batch embedding
        return List.of();
    }

    @Override
    public int getDimensions() {
        // TODO: Return model dimensions
        return 768;
    }

    @Override
    public String getModelName() {
        return "spring-ai-embedding";
    }
}
