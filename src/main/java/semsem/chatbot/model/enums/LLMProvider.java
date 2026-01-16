package semsem.chatbot.model.enums;

/**
 * Supported LLM providers for chat generation.
 */
public enum LLMProvider {
    // Cloud providers
    GEMINI("gemini", "Google Gemini", true),
    COHERE("cohere", "Cohere", true),

    // Local providers
    OLLAMA("ollama", "Ollama Local", false);

    private final String key;
    private final String displayName;
    private final boolean cloud;

    LLMProvider(String key, String displayName, boolean cloud) {
        this.key = key;
        this.displayName = displayName;
        this.cloud = cloud;
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

    public static LLMProvider fromKey(String key) {
        for (LLMProvider provider : values()) {
            if (provider.key.equalsIgnoreCase(key)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown LLM provider: " + key);
    }
}
