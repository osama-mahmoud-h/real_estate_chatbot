package semsem.chatbot.orchestration.graph;

import java.util.Map;

/**
 * Immutable state container for graph execution.
 * Holds all data passed between nodes during workflow execution.
 */
public interface GraphState {

    Map<String, Object> getData();

    GraphState withData(String key, Object value);

    <T> T get(String key, Class<T> type);

    GraphState merge(GraphState other);
}
