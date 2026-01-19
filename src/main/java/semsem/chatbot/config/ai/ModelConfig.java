package semsem.chatbot.config.ai;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Unified model configuration for both chat and embedding models.
 * Type field determines which properties are relevant.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelConfig extends AbstractModelConfig {

    // =========================================================================
    // CHAT MODEL PROPERTIES
    // =========================================================================

    /** Context window size in tokens (chat models) */
    private int contextWindow;

    /** Maximum output tokens (chat models) */
    private int maxOutputTokens;

    // =========================================================================
    // EMBEDDING MODEL PROPERTIES
    // =========================================================================

    /** Vector dimensions (embedding models) */
    private int dimensions;

    /** Maximum input tokens (embedding models) */
    private int maxInputTokens;

    // =========================================================================
    // TYPE CHECKS
    // =========================================================================

    public boolean isChat() {
        return "chat".equalsIgnoreCase(type);
    }

    public boolean isEmbedding() {
        return "embedding".equalsIgnoreCase(type);
    }

    // =========================================================================
    // CHAT CAPABILITY SHORTCUTS
    // =========================================================================

    public boolean supportsStreaming() {
        return hasCapability(ModelCapability.STREAMING);
    }

    public boolean supportsFunctionCalling() {
        return hasCapability(ModelCapability.FUNCTION_CALLING);
    }

    public boolean supportsVision() {
        return hasCapability(ModelCapability.VISION);
    }

    public boolean supportsJsonMode() {
        return hasCapability(ModelCapability.JSON_MODE);
    }

    public boolean supportsSystemPrompt() {
        return hasCapability(ModelCapability.SYSTEM_PROMPT);
    }

    // =========================================================================
    // EMBEDDING CAPABILITY SHORTCUTS
    // =========================================================================

    public boolean supportsBatch() {
        return hasCapability(ModelCapability.BATCH);
    }

    public boolean supportsMultilingual() {
        return hasCapability(ModelCapability.MULTILINGUAL);
    }
}