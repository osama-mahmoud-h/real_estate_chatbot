package semsem.chatbot.service.embedding.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.List;

/**
 * Ollama Embedding Strategy (Local).
 */
@Slf4j
@Component
public class OllamaEmbeddingStrategy extends BaseEmbeddingStrategy {

    @Value("${embedding.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${embedding.ollama.model:nomic-embed-text}")
    private String model;

    private static final int OLLAMA_DIMENSIONS = 768;

    // TODO: Inject WebClient for Ollama Embedding API

    @Override
    public float[] embed(String text) {
        // TODO: Implement using Ollama Embedding API
        log.debug("Embedding with Ollama model: {}", model);
        return new float[OLLAMA_DIMENSIONS];
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        // TODO: Implement batch embedding using Ollama API
        log.debug("Batch embedding {} texts with Ollama model: {}", texts.size(), model);
        return texts.stream()
                .map(this::embed)
                .toList();
    }

    @Override
    public int getDimensions() {
        return OLLAMA_DIMENSIONS;
    }

    @Override
    public EmbeddingProvider getProvider() {
        return EmbeddingProvider.OLLAMA;
    }

    @Override
    public String getModelName() {
        return model;
    }

    @Override
    public boolean isAvailable() {
        // TODO: Check if Ollama server is reachable
        return baseUrl != null && !baseUrl.isBlank();
    }
}
