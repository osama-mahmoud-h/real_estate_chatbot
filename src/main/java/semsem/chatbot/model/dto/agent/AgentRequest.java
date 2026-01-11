package semsem.chatbot.model.dto.agent;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Request for agent execution.
 */
@Data
@Builder
public class AgentRequest {

    private String input;
    private String conversationId;
    private String agentType; // react, plan_execute, conversational
    private List<String> enabledTools;
    private Map<String, Object> context;
    private Integer maxIterations;
}
