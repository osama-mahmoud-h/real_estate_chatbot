package semsem.chatbot.model.enums;

/**
 * Supported embedding providers.
 */
public enum EmbeddingProvider {
    // Cloud providers
    GEMINI("gemini", "Google Gemini", true, 768),
    COHERE("cohere", "Cohere", true, 1024),

    // Local providers
    OLLAMA("ollama", "Ollama Local", false, 768);

    private final String key;
    private final String displayName;
    private final boolean cloud;
    private final int defaultDimensions;

    EmbeddingProvider(String key, String displayName, boolean cloud, int defaultDimensions) {
        this.key = key;
        this.displayName = displayName;
        this.cloud = cloud;
        this.defaultDimensions = defaultDimensions;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCloud() {
        return cloud;
    }

    public boolean isLocal() {
        return !cloud;
    }

    public int getDefaultDimensions() {
        return defaultDimensions;
    }

    public static EmbeddingProvider fromKey(String key) {
        for (EmbeddingProvider provider : values()) {
            if (provider.key.equalsIgnoreCase(key)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown embedding provider: " + key);
    }
}
