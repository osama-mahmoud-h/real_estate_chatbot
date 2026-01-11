package semsem.chatbot.service.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Ollama local embedding service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaEmbeddingService implements EmbeddingService {

    // TODO: Inject Ollama embedding client
    private String modelName = "nomic-embed-text";

    @Override
    public float[] embed(String text) {
        // TODO: Implement using Ollama
        return new float[0];
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        // TODO: Implement batch embedding
        return List.of();
    }

    @Override
    public int getDimensions() {
        return 768;
    }

    @Override
    public String getModelName() {
        return modelName;
    }
}
