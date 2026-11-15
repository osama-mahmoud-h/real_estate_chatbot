package semsem.chatbot.llm.domain.enums;

/**
 * Capabilities specific to embedding models.
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

    @Override
    public String getValue() {
        return value;
    }
}
