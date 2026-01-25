package semsem.chatbot.config.llm;

import lombok.Data;
import semsem.chatbot.config.llm.chat.ChatModelConfig;
import semsem.chatbot.config.llm.embedding.EmbeddingModelConfig;
import semsem.chatbot.model.enums.ModelType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base for provider configurations.
 * Contains credentials and models with capabilities.
 * Implements IProviderConfig interface.
 *
 * Uses raw maps for YAML binding, converts to proper types on access.
 */
@Data
public abstract class AbstractProviderConfig implements IProviderConfig {

    /** API key (null for local providers like Ollama) */
    protected String apiKey;

    /** Base URL for API calls */
    protected String baseUrl;

    /** Raw model configs from YAML (Spring Boot binds to this) */
    protected Map<String, Map<String, Object>> models = new HashMap<>();

    /** Cache for converted model configs */
    private transient Map<String, AbstractModelConfig> modelCache = new ConcurrentHashMap<>();

    // =========================================================================
    // MODEL ACCESS (IProviderConfig implementation)
    // =========================================================================

    @Override
    public Optional<AbstractModelConfig> getModel(String modelName) {
        if (!models.containsKey(modelName)) {
            return Optional.empty();
        }

        // Use cache if available
        if (modelCache.containsKey(modelName)) {
            return Optional.of(modelCache.get(modelName));
        }

        // Convert and cache
        Map<String, Object> raw = models.get(modelName);
        AbstractModelConfig config = convertToModelConfig(raw);
        if (config != null) {
            modelCache.put(modelName, config);
        }
        return Optional.ofNullable(config);
    }

    /**
     * Get a chat model by name.
     */
    public Optional<ChatModelConfig> getChatModel(String modelName) {
        return getModel(modelName)
                .filter(m -> m instanceof ChatModelConfig)
                .map(m -> (ChatModelConfig) m);
    }

    /**
     * Get an embedding model by name.
     */
    public Optional<EmbeddingModelConfig> getEmbeddingModel(String modelName) {
        return getModel(modelName)
                .filter(m -> m instanceof EmbeddingModelConfig)
                .map(m -> (EmbeddingModelConfig) m);
    }

    public AbstractModelConfig getModelOrThrow(String modelName) {
        return getModel(modelName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Model not found: " + modelName));
    }

    public boolean hasModel(String modelName) {
        return models.containsKey(modelName);
    }

    // =========================================================================
    // CONVERSION HELPERS
    // =========================================================================

    private AbstractModelConfig convertToModelConfig(Map<String, Object> raw) {
        if (raw == null) return null;

        String type = getStringValue(raw, "type", "chat");

        if (ModelType.EMBEDDING.toString().toLowerCase().equalsIgnoreCase(type)) {
            return createEmbeddingModelConfig(raw);
        } else if(ModelType.CHAT.toString().toLowerCase().equalsIgnoreCase(type)) {
            return createChatModelConfig(raw);
        }
        throw new IllegalArgumentException("Unknown model type: " + type);
    }

    private ChatModelConfig createChatModelConfig(Map<String, Object> raw) {

        String type = getStringValue(raw, "type", ModelType.CHAT.toString().toLowerCase());
        if (! ModelType.CHAT.toString().toLowerCase().equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Model type mismatch: expected chat model, got embedding");
        }

        ChatModelConfig config = ChatModelConfig.builder()
                        .type(ModelType.CHAT)
                        .contextWindow(getIntValue(raw, "contextWindow", 0))
                        .maxOutputTokens(getIntValue(raw, "maxOutputTokens", 0))
                        .capabilities(getListValue(raw, "capabilities"))
                        .properties(getMapValue(raw, "properties"))
                        .build();

        return config;
    }

    private EmbeddingModelConfig createEmbeddingModelConfig(Map<String, Object> raw) {
        String type = getStringValue(raw, "type", ModelType.EMBEDDING.toString().toLowerCase());
        if (! ModelType.EMBEDDING.toString().toLowerCase().equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Model type mismatch: expected embedding model, got chat");
        }

        EmbeddingModelConfig config = EmbeddingModelConfig.builder()
                        .type(ModelType.EMBEDDING)
                        .dimensions(getIntValue(raw, "dimensions", 0))
                        .maxInputTokens(getIntValue(raw, "maxInputTokens", 0))
                        .capabilities(getListValue(raw, "capabilities"))
                        .properties(getMapValue(raw, "properties"))
                        .build();

        return config;
    }

    private String getStringValue(Map<String, Object> source, String key, String defaultValue) {
        Object value = source.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private int getIntValue(Map<String, Object> source, String key, int defaultValue) {
        Object value = source.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getListValue(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapValue(Map<String, Object> source, String key) {
        Object value = source.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Map.of();
    }

    // =========================================================================
    // AVAILABILITY (IProviderConfig implementation)
    // =========================================================================

    @Override
    public abstract boolean isAvailable();

    @Override
    public boolean isCloud() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public boolean isLocal() {
        return !isCloud();
    }
}