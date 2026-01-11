package semsem.chatbot.service.agent;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a single step in agent execution.
 */
@Data
@Builder
public class AgentStep {

    private int stepNumber;
    private String thought;
    private String action;
    private String actionInput;
    private String observation;
    private long latencyMs;
}
