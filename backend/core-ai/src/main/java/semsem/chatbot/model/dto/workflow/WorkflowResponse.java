package semsem.chatbot.model.dto.workflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Response from workflow execution.
 */
@Data
@Builder
public class WorkflowResponse {

    private String workflowId;
    private String threadId;
    private Map<String, Object> outputs;
    private List<NodeExecution> nodeExecutions;
    private long totalLatencyMs;
    private boolean success;
    private String errorMessage;

    @Data
    @Builder
    public static class NodeExecution {
        private String nodeName;
        private Map<String, Object> inputs;
        private Map<String, Object> outputs;
        private long latencyMs;
    }
}
