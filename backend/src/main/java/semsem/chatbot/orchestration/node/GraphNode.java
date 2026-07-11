package semsem.chatbot.orchestration.node;

import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;

/**
 * Base interface for all graph nodes.
 * Each node processes state and returns updated state.
 */
public interface GraphNode<S extends GraphState> {

    GraphNodeNames getName();

    S execute(S state);
}
