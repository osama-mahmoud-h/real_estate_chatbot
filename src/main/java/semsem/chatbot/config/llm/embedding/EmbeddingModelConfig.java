package semsem.chatbot.config.llm.embedding;

import lombok.Data;
import lombok.EqualsAndHashCode;
import semsem.chatbot.config.llm.AbstractModelConfig;

/**
 * Configuration for embedding models.
 * Single responsibility: holds embedding-specific model properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmbeddingModelConfig extends AbstractModelConfig implements IEmbeddingModelConfig {

    /** Vector dimensions */
    private int dimensions;

    /** Maximum input tokens */
    private int maxInputTokens;

    // =========================================================================
    // CAPABILITY SHORTCUTS
    // =========================================================================

    @Override
    public boolean supportsBatch() {
        return hasCapability(EmbeddingModelCapability.BATCH);
    }

    @Override
    public boolean supportsMultilingual() {
        return hasCapability(EmbeddingModelCapability.MULTILINGUAL);
    }
}