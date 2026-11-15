package semsem.chatbot.embedding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.embedding.strategy.EmbeddingFactory;
import semsem.chatbot.embedding.strategy.EmbeddingStrategy;

import java.util.Collections;
import java.util.List;

/**
 * Default embedding service implementation that delegates to the configured EmbeddingStrategy.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultEmbeddingService implements EmbeddingService {

    private final EmbeddingFactory embeddingFactory;

    private EmbeddingStrategy getStrategy() {
        return embeddingFactory.getAvailableStrategy();
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            log.warn("Requested embedding for null or blank text");
            return new float[0];
        }
        try {
            EmbeddingStrategy strategy = getStrategy();
            log.debug("Generating single embedding via provider='{}', model='{}' (textLength={})",
                    strategy.getProvider(), strategy.getModelName(), text.length());
            return strategy.embed(text);
        } catch (Exception ex) {
            log.error("Failed to generate embedding for text (length={}): {}",
                    text.length(), ex.getMessage(), ex);
            throw new IllegalStateException("Embedding generation failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            log.warn("Requested batch embedding for null or empty text list");
            return Collections.emptyList();
        }
        try {
            EmbeddingStrategy strategy = getStrategy();
            log.debug("Generating batch embeddings for {} texts via provider='{}', model='{}'",
                    texts.size(), strategy.getProvider(), strategy.getModelName());
            return strategy.embedBatch(texts);
        } catch (Exception ex) {
            log.error("Failed to generate batch embeddings for {} texts: {}",
                    texts.size(), ex.getMessage(), ex);
            throw new IllegalStateException("Batch embedding generation failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int getDimensions() {
        try {
            return getStrategy().getDimensions();
        } catch (Exception ex) {
            log.error("Failed to resolve embedding dimensions: {}", ex.getMessage(), ex);
            return 0;
        }
    }

    @Override
    public String getModelName() {
        try {
            return getStrategy().getModelName();
        } catch (Exception ex) {
            log.error("Failed to resolve embedding model name: {}", ex.getMessage(), ex);
            return "unknown";
        }
    }
}
