package semsem.chatbot.orchestration.checkpointer;

import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Optional;

/**
 * Interface for persisting graph execution state.
 * Enables resumable workflows and state recovery.
 */
public interface Checkpointer {

    void save(String threadId, GraphNodeNames nodeId, GraphState state);

    Optional<GraphState> load(String threadId, GraphNodeNames nodeId);

    Optional<GraphState> loadLatest(String threadId);

    void delete(String threadId);

    boolean exists(String threadId);
}
