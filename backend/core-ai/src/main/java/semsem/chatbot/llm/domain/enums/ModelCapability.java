package semsem.chatbot.llm.domain.enums;

/**
 * Base interface for model capabilities.
 * Implemented by {@link ChatModelCapability} and {@link EmbeddingModelCapability}.
 */
public interface ModelCapability {

    String getValue();
}
