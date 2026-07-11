package semsem.chatbot.orchestration.graph;

import lombok.Builder;
import lombok.Data;
import semsem.chatbot.model.enums.GraphNodeNames;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Typed metadata for graph state execution.
 * Tracks execution context, timing, and node history.
 */
@Data
@Builder
public class StateMetadata {

    private String threadId;
    private String userId;
    private String sessionId;

    @Builder.Default
    private Instant startTime = Instant.now();

    private Instant endTime;

    @Builder.Default
    private List<GraphNodeNames> executedNodes = new ArrayList<>();

    private GraphNodeNames currentNode;
    private GraphNodeNames previousNode;

    private Integer totalSteps;
    private Integer currentStep;

    private String errorMessage;
    private boolean hasError;

    private String languageDetected;
    private String intentClassified;
    private Double confidenceScore;

    public void markNodeExecuted(GraphNodeNames node) {
        if (executedNodes == null) {
            executedNodes = new ArrayList<>();
        }
        previousNode = currentNode;
        currentNode = node;
        executedNodes.add(node);
        if (currentStep == null) {
            currentStep = 1;
        } else {
            currentStep++;
        }
    }

    public void markError(String message) {
        this.hasError = true;
        this.errorMessage = message;
    }

    public void markComplete() {
        this.endTime = Instant.now();
    }

    public long getDurationMs() {
        if (startTime == null) {
            return 0;
        }
        Instant end = endTime != null ? endTime : Instant.now();
        return end.toEpochMilli() - startTime.toEpochMilli();
    }
}