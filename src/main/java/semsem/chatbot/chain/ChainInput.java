package semsem.chatbot.chain;

import java.util.Map;

/**
 * Base interface for chain input.
 * Implementations define typed fields for specific chain inputs.
 * Provides compile-time safety instead of string-keyed maps.
 */
public interface ChainInput {

    // ==================== Context ====================

    /**
     * Returns the conversation/session identifier.
     */
    String getConversationId();

    /**
     * Returns the user query or prompt.
     */
    String getQuery();

    // ==================== Variables ====================

    /**
     * Returns additional input variables as key-value pairs.
     * Use typed getters in implementations for compile-time safety.
     */
    Map<String, Object> getVariables();

    // ==================== Validation ====================

    /**
     * Validates the input before chain execution.
     *
     * @return true if input is valid
     */
    default boolean isValid() {
        return getQuery() != null && !getQuery().isBlank();
    }
}