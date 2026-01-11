package semsem.chatbot.model.dto.workflow;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request for workflow execution.
 */
@Data
@Builder
public class WorkflowRequest {

    private String workflowId;
    private String threadId;
    private Map<String, Object> inputs;
    private Map<String, Object> config;
    private Boolean stream;
}
