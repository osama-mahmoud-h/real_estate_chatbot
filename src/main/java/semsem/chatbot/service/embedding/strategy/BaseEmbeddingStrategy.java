package semsem.chatbot.service.embedding.strategy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base implementation for EmbeddingStrategy with common functionality.
 */
@Slf4j
@Getter
@Setter
public abstract class BaseEmbeddingStrategy implements EmbeddingStrategy {

    protected String modelName;
    protected int dimensions;

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        // Default implementation: embed one by one
        // Subclasses can override for batch API calls
        return texts.stream()
                .map(this::embed)
                .collect(Collectors.toList());
    }
}
