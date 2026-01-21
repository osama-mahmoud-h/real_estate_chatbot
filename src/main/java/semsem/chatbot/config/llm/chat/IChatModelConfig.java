package semsem.chatbot.config.llm.chat;

import semsem.chatbot.config.llm.IModelConfig;

/**
 * Interface for chat model configurations.
 * Extends IModelConfig with chat-specific properties.
 */
public interface IChatModelConfig extends IModelConfig {

    /**
     * Get the context window size in tokens.
     */
    int getContextWindow();

    /**
     * Get the maximum output tokens.
     */
    int getMaxOutputTokens();

    /**
     * Check if the model supports streaming responses.
     */
    boolean supportsStreaming();

    /**
     * Check if the model supports function calling.
     */
    boolean supportsFunctionCalling();

    /**
     * Check if the model supports vision/image input.
     */
    boolean supportsVision();

    /**
     * Check if the model supports JSON mode output.
     */
    boolean supportsJsonMode();

    /**
     * Check if the model supports system prompts.
     */
    boolean supportsSystemPrompt();
}