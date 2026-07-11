package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Output from SQL_EXECUTOR node.
 */
@Data
@Builder
public class SqlExecutorOutput {

    private List<Map<String, Object>> results;
    private int rowCount;
    private long executionTimeMs;
    private boolean success;
    private String errorMessage;
}
