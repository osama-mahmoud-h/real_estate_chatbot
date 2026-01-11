package semsem.chatbot.orchestration.node;

import semsem.chatbot.orchestration.graph.GraphState;

/**
 * Base interface for all graph nodes.
 * Each node processes state and returns updated state.
 */
public interface GraphNode<S extends GraphState> {

    String getName();

    S execute(S state);
}
