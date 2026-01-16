package semsem.chatbot.service.embedding.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.List;

/**
 * Google Gemini Embedding Strategy (Cloud).
 */
@Slf4j
@Component
public class GeminiEmbeddingStrategy extends BaseEmbeddingStrategy {

    @Value("${embedding.gemini.api-key:}")
    private String apiKey;

    @Value("${embedding.gemini.model:text-embedding-004}")
    private String model;

    @Value("${embedding.gemini.base-url:https://generativelanguage.googleapis.com}")
    private String baseUrl;

    private static final int GEMINI_DIMENSIONS = 768;

    // TODO: Inject WebClient for Gemini Embedding API

    @Override
    public float[] embed(String text) {
        // TODO: Implement using Google Gemini Embedding API
        log.debug("Embedding with Gemini model: {}", model);
        return new float[GEMINI_DIMENSIONS];
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        // TODO: Implement batch embedding using Google Gemini API
        log.debug("Batch embedding {} texts with Gemini model: {}", texts.size(), model);
        return texts.stream()
                .map(this::embed)
                .toList();
    }

    @Override
    public int getDimensions() {
        return GEMINI_DIMENSIONS;
    }

    @Override
    public EmbeddingProvider getProvider() {
        return EmbeddingProvider.GEMINI;
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
