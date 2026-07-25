package semsem.chatbot.orchestration.dbchat.output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Output from SQL_GENERATOR node.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SqlGeneratorOutput implements Serializable {

    @JsonProperty("sql")
    private String generatedSql;

    private Map<String, Object> parameters;

    @JsonProperty("tables_used")
    private List<String> tablesUsed;

    @JsonProperty("is_safe")
    @Builder.Default
    private boolean isSafe = true;

    private String explanation;

    @JsonProperty("estimated_complexity")
    private String estimatedComplexity;

    public static SqlGeneratorOutput noSql(String intentName) {
        return builder()
                .isSafe(true)
                .explanation("Intent " + intentName + " does not require SQL query")
                .build();
    }

    public static SqlGeneratorOutput error(String message) {
        return builder()
                .isSafe(false)
                .explanation("Error: " + message)
                .build();
    }

    public static SqlGeneratorOutput skipped(String reason) {
        return builder()
                .isSafe(true)
                .explanation("Skipped: " + reason)
                .build();
    }

    /**
     * Check if query has parameters.
     */
    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    /**
     * Check if query is valid and safe to execute.
     */
    public boolean isExecutable() {
        return generatedSql != null && !generatedSql.isBlank() && isSafe;
    }
}