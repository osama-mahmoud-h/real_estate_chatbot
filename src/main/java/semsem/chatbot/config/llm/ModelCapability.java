package semsem.chatbot.config.llm;

/**
 * Base interface for model capabilities.
 * Implemented by ChatModelCapability and EmbeddingModelCapability enums.
 */
public interface ModelCapability {

    /**
     * Get the string value of this capability.
     */
    String getValue();
}