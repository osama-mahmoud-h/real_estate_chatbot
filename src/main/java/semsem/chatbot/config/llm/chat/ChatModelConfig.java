package semsem.chatbot.config.llm.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import semsem.chatbot.config.llm.AbstractModelConfig;

/**
 * Configuration for chat/LLM models.
 * Single responsibility: holds chat-specific model properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatModelConfig extends AbstractModelConfig implements IChatModelConfig {

    /** Context window size in tokens */
    private int contextWindow;

    /** Maximum output tokens */
    private int maxOutputTokens;

    // =========================================================================
    // CAPABILITY SHORTCUTS
    // =========================================================================

    @Override
    public boolean supportsStreaming() {
        return hasCapability(ChatModelCapability.STREAMING);
    }

    @Override
    public boolean supportsFunctionCalling() {
        return hasCapability(ChatModelCapability.FUNCTION_CALLING);
    }

    @Override
    public boolean supportsVision() {
        return hasCapability(ChatModelCapability.VISION);
    }

    @Override
    public boolean supportsJsonMode() {
        return hasCapability(ChatModelCapability.JSON_MODE);
    }

    @Override
    public boolean supportsSystemPrompt() {
        return hasCapability(ChatModelCapability.SYSTEM_PROMPT);
    }
}