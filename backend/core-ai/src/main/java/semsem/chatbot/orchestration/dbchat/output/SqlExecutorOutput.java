package semsem.chatbot.orchestration.dbchat.output;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SqlExecutorOutput implements Serializable {

    private List<Map<String, Object>> results;
    private int rowCount;
    private long executionTimeMs;
    private boolean success;
    private String errorMessage;

    public static SqlExecutorOutput success(List<Map<String, Object>> results, long executionTimeMs) {
        return builder()
                .results(results)
                .rowCount(results.size())
                .executionTimeMs(executionTimeMs)
                .success(true)
                .build();
    }

    public static SqlExecutorOutput failure(String errorMessage, long executionTimeMs) {
        return builder()
                .results(List.of())
                .rowCount(0)
                .executionTimeMs(executionTimeMs)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    public static SqlExecutorOutput skipped(String reason) {
        return builder()
                .results(List.of())
                .rowCount(0)
                .executionTimeMs(0)
                .success(true)
                .errorMessage(reason)
                .build();
    }
}
