package semsem.chatbot.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for chat endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Chat response")
public class ChatResponseDto {

    @Schema(description = "Generated response message")
    private String message;

    @Schema(description = "Conversation ID")
    private Long conversationId;

    @Schema(description = "Detected user intent")
    private String intent;

    @Schema(description = "Intent confidence score")
    private Double intentConfidence;

    @Schema(description = "Number of results found (if applicable)")
    private Integer resultCount;

    @Schema(description = "Query execution time in milliseconds")
    private Long executionTimeMs;

    @Schema(description = "SQL query results (if applicable)")
    private List<Map<String, Object>> data;

    @Schema(description = "Debug information (only in debug mode)")
    private DebugInfo debug;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Debug information")
    public static class DebugInfo {
        private String generatedSql;
        private Map<String, Object> sqlParameters;
        private String detectedLanguage;
        private Object extractedEntities;
    }
}