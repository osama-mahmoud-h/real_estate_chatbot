package semsem.chatbot.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * User intent classifications for real estate chatbot.
 * Read-only intents only - no write operations.
 */
@Getter
@RequiredArgsConstructor
public enum UserIntent {

    // Property Search Intents
    SEARCH_PROPERTIES("Search for properties with filters", true),
    VIEW_PROPERTY_DETAILS("View details of a specific property", true),
    COMPARE_PROPERTIES("Compare multiple properties", true),
    GET_FEATURED_PROPERTIES("Get featured/highlighted properties", true),
    GET_RECENT_LISTINGS("Get recently listed properties", true),

    // Statistics & Analytics
    GET_PROPERTY_STATS("Get market statistics and aggregations", true),

    // Agent & Features
    GET_AGENT_INFO("Get information about agents", true),
    LIST_FEATURES("List available amenities/features", true),

    // Non-SQL Intents
    GENERAL_QUESTION("General real estate advice/questions", false),
    GREETING("Greeting or conversation start", false),
    OUT_OF_SCOPE("Query not related to real estate", false);

    private final String description;
    private final boolean requiresSql;

    /**
     * Parse intent from string (case-insensitive).
     */
    public static UserIntent fromString(String value) {
        if (value == null || value.isBlank()) {
            return OUT_OF_SCOPE;
        }
        try {
            return valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return OUT_OF_SCOPE;
        }
    }

    /**
     * Check if this intent requires database query.
     */
    public boolean needsDatabaseQuery() {
        return requiresSql;
    }
}