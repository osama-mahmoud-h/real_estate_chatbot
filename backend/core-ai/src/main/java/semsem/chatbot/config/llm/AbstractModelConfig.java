package semsem.chatbot.config.llm;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import semsem.chatbot.model.enums.ModelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base for model configurations.
 * Provides common properties and capability checking.
 * Subclasses (ChatModelConfig, EmbeddingModelConfig) add model-specific properties.
 */
@Data
@SuperBuilder
public abstract class AbstractModelConfig implements IModelConfig {

    /** Model type identifier */
    protected ModelType type;

    /** Model capabilities (stored as strings for YAML binding compatibility) */
    protected List<String> capabilities = new ArrayList<>();

    /** Flexible extra properties */
    protected Map<String, Object> properties = new HashMap<>();

    // =========================================================================
    // CAPABILITY CHECKING (IModelConfig interface implementation)
    // =========================================================================

    @Override
    public boolean hasCapability(ModelCapability capability) {
        if (capabilities == null || capability == null) {
            return false;
        }
        return capabilities.contains(capability.getValue());
    }

    @Override
    public boolean hasAllCapabilities(ModelCapability... required) {
        if (required == null) {
            return true;
        }
        for (ModelCapability cap : required) {
            if (!hasCapability(cap)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasAnyCapability(ModelCapability... required) {
        if (required == null) {
            return false;
        }
        for (ModelCapability cap : required) {
            if (hasCapability(cap)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // STRING-BASED CAPABILITY CHECKING (for backward compatibility)
    // =========================================================================

    /**
     * Check if the model has a specific capability by string value.
     * Prefer using hasCapability(ModelCapability) for type safety.
     */
    public boolean hasCapability(String capability) {
        return capabilities != null && capabilities.contains(capability);
    }

    /**
     * Check if the model has all specified capabilities by string values.
     * Prefer using hasAllCapabilities(ModelCapability...) for type safety.
     */
    public boolean hasAllCapabilities(String... required) {
        for (String cap : required) {
            if (!hasCapability(cap)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the model has any of the specified capabilities by string values.
     * Prefer using hasAnyCapability(ModelCapability...) for type safety.
     */
    public boolean hasAnyCapability(String... required) {
        for (String cap : required) {
            if (hasCapability(cap)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // PROPERTY ACCESS (type-safe with defaults)
    // =========================================================================

    public int getInt(String key, int defaultValue) {
        Object value = properties.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        Object value = properties.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        Object value = properties.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        Object value = properties.get(key);
        if (value == null) return defaultValue;
        try {
            return (T) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
}