package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

/**
 * Output from RESPONSE_GENERATOR node.
 */
@Data
@Builder
public class ResponseGeneratorOutput {

    private String generatedResponse;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private long latencyMs;
    private String modelUsed;
}
