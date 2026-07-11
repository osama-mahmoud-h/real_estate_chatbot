package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Output from FINAL_RESPONSE_BUILDER node.
 */
@Data
@Builder
public class FinalResponseBuilderOutput {

    private String finalResponse;
    private String formattedResponse;
    private List<Map<String, Object>> suggestedActions;
    private Map<String, Object> responseMetadata;
}
