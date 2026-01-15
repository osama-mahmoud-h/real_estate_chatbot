package semsem.chatbot.service.embedding.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.List;

/**
 * Cohere Embedding Strategy (Cloud).
 */
@Slf4j
@Component
public class CohereEmbeddingStrategy extends BaseEmbeddingStrategy {

    @Value("${embedding.cohere.api-key:}")
    private String apiKey;

    @Value("${embedding.cohere.model:embed-english-v3.0}")
    private String model;

    @Value("${embedding.cohere.base-url:https://api.cohere.ai}")
    private String baseUrl;

    private static final int COHERE_DIMENSIONS = 1024;

    // TODO: Inject WebClient for Cohere Embedding API

    @Override
    public float[] embed(String text) {
        // TODO: Implement using Cohere Embedding API
        log.debug("Embedding with Cohere model: {}", model);
        return new float[COHERE_DIMENSIONS];
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        // TODO: Implement batch embedding using Cohere API
        // Cohere supports batch embedding natively
        log.debug("Batch embedding {} texts with Cohere model: {}", texts.size(), model);
        return texts.stream()
                .map(this::embed)
                .toList();
    }

    @Override
    public int getDimensions() {
        return COHERE_DIMENSIONS;
    }

    @Override
    public EmbeddingProvider getProvider() {
        return EmbeddingProvider.COHERE;
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
}
