package semsem.chatbot.config.llm.embedding;

import semsem.chatbot.config.llm.ModelCapability;

/**
 * Enum for embedding model capabilities.
 * Single responsibility: defines capabilities specific to embedding models.
 */
public enum EmbeddingModelCapability implements ModelCapability {

    BATCH("batch"),
    MULTILINGUAL("multilingual"),
    SEARCH_DOCUMENT("search-document"),
    SEARCH_QUERY("search-query");

    private final String value;

    EmbeddingModelCapability(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EmbeddingModelCapability fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Capability value cannot be null");
        }
        for (EmbeddingModelCapability cap : values()) {
            if (cap.value.equalsIgnoreCase(value)) {
                return cap;
            }
        }
        throw new IllegalArgumentException("Unknown embedding capability: " + value);
    }

    public static EmbeddingModelCapability fromValueOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (EmbeddingModelCapability cap : values()) {
            if (cap.value.equalsIgnoreCase(value)) {
                return cap;
            }
        }
        return null;
    }
}