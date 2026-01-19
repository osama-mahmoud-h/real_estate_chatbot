package semsem.chatbot.config.ai;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base for model configurations.
 * Provides common properties and capability checking.
 * Subclasses can add model-specific properties.
 */
@Data
public abstract class AbstractModelConfig {

    /** Model type identifier */
    protected String type;

    /** Model capabilities */
    protected List<String> capabilities = new ArrayList<>();

    /** Flexible extra properties */
    protected Map<String, Object> properties = new HashMap<>();

    // =========================================================================
    // CAPABILITY CHECKING
    // =========================================================================

    public boolean hasCapability(String capability) {
        return capabilities != null && capabilities.contains(capability);
    }

    public boolean hasAllCapabilities(String... required) {
        for (String cap : required) {
            if (!hasCapability(cap)) {
                return false;
            }
        }
        return true;
    }

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