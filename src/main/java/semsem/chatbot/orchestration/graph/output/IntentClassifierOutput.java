package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Output from INTENT_CLASSIFIER node.
 */
@Data
@Builder
public class IntentClassifierOutput {

    private String intent;
    private double confidence;
    private Map<String, Double> allIntentScores;
}
