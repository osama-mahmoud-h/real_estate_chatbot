package semsem.chatbot.tool;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Result from tool execution.
 */
@Data
@Builder
public class ToolResult {

    private String toolName;
    private boolean success;
    private String output;
    private String errorMessage;
    private Map<String, Object> data;
    private long executionTimeMs;

    public static ToolResult success(String output) {
        return ToolResult.builder()
                .success(true)
                .output(output)
                .build();
    }

    public static ToolResult success(String output, Map<String, Object> data) {
        return ToolResult.builder()
                .success(true)
                .output(output)
                .data(data)
                .build();
    }

    public static ToolResult failure(String errorMessage) {
        return ToolResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
