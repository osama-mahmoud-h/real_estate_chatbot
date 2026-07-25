package semsem.chatbot.service.embedding;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatbot.service.embedding.strategy.EmbeddingFactory;
import semsem.chatbot.service.embedding.strategy.EmbeddingStrategy;

import java.util.List;

/**
 * Default embedding service implementation that delegates to the configured EmbeddingStrategy.
 */
@Service
@RequiredArgsConstructor
public class DefaultEmbeddingService implements EmbeddingService {

    private final EmbeddingFactory embeddingFactory;

    private EmbeddingStrategy getStrategy() {
        return embeddingFactory.getAvailableStrategy();
    }

    @Override
    public float[] embed(String text) {
        return getStrategy().embed(text);
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        return getStrategy().embedBatch(texts);
    }

    @Override
    public int getDimensions() {
        return getStrategy().getDimensions();
    }

    @Override
    public String getModelName() {
        return getStrategy().getModelName();
    }
}
