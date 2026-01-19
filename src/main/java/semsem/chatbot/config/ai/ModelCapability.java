package semsem.chatbot.config.ai;

/**
 * Constants for model capabilities.
 */
public final class ModelCapability {

    // Chat capabilities
    public static final String STREAMING = "streaming";
    public static final String FUNCTION_CALLING = "function-calling";
    public static final String VISION = "vision";
    public static final String JSON_MODE = "json-mode";
    public static final String SYSTEM_PROMPT = "system-prompt";
    public static final String RAG = "rag";
    public static final String CODE_GENERATION = "code-generation";

    // Embedding capabilities
    public static final String BATCH = "batch";
    public static final String MULTILINGUAL = "multilingual";
    public static final String SEARCH_DOCUMENT = "search-document";
    public static final String SEARCH_QUERY = "search-query";

    private ModelCapability() {}
}