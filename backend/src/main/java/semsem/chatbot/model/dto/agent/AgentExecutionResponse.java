package semsem.chatbot.model.dto.agent;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Response from agent execution.
 */
@Data
@Builder
public class AgentExecutionResponse {

    private String output;
    private List<ExecutionStep> steps;
    private int totalTokens;
    private long latencyMs;
    private boolean success;
    private String errorMessage;

    @Data
    @Builder
    public static class ExecutionStep {
        private int stepNumber;
        private String thought;
        private String action;
        private Map<String, Object> actionInput;
        private String observation;
    }
}
