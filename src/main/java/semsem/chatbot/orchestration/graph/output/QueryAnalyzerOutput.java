package semsem.chatbot.orchestration.graph.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatbot.model.enums.UserIntent;

import java.math.BigDecimal;
import java.util.List;

/**
 * Combined output from query analysis: intent classification + entity extraction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryAnalyzerOutput {

    private IntentResult intent;
    private ExtractedEntities entities;
    private List<ExtractedToken> extractedTokens;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IntentResult {
        private String name;
        private double confidence;
        @JsonProperty("requires_sql")
        private boolean requiresSql;
        private String reasoning;

        public UserIntent toUserIntent() {
            return UserIntent.fromString(name);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtractedEntities {
        @JsonProperty("property_type")
        private String propertyType;

        @JsonProperty("listing_type")
        private String listingType;

        private LocationEntity location;

        @JsonProperty("price_range")
        private PriceRange priceRange;

        private RangeValue bedrooms;
        private RangeValue bathrooms;

        @JsonProperty("area_sqft")
        private RangeValue areaSqft;

        private List<String> features;

        @JsonProperty("year_built")
        private RangeValue yearBuilt;

        @JsonProperty("property_status")
        private String propertyStatus;

        @JsonProperty("property_ids")
        private List<Long> propertyIds;

        @JsonProperty("agent_id")
        private Long agentId;

        @JsonProperty("sort_by")
        private String sortBy;

        private Integer limit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationEntity {
        private String city;
        private String state;
        private String country;
        private String neighborhood;
        @JsonProperty("postal_code")
        private String postalCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceRange {
        private BigDecimal min;
        private BigDecimal max;
        @Builder.Default
        private String currency = "USD";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RangeValue {
        private Integer min;
        private Integer max;
        private Integer exact;

        public boolean hasValue() {
            return min != null || max != null || exact != null;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtractedToken {
        private String type;
        private String value;
        @JsonProperty("original_text")
        private String originalText;
    }

    /**
     * Get the UserIntent enum from parsed result.
     */
    public UserIntent getUserIntent() {
        return intent != null ? intent.toUserIntent() : UserIntent.OUT_OF_SCOPE;
    }

    /**
     * Check if SQL query is required.
     */
    public boolean requiresSql() {
        return intent != null && intent.isRequiresSql();
    }
}