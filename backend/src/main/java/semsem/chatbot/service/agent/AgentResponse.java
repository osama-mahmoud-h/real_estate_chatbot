package semsem.chatbot.service.agent;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Response from agent execution.
 */
@Data
@Builder
public class AgentResponse {

    private String output;
    private List<AgentStep> steps;
    private Map<String, Object> metadata;
    private int totalTokens;
    private long latencyMs;
    private boolean success;
    private String errorMessage;

    public static AgentResponse success(String output, List<AgentStep> steps) {
        return AgentResponse.builder()
                .output(output)
                .steps(steps)
                .success(true)
                .build();
    }

    public static AgentResponse failure(String errorMessage) {
        return AgentResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
