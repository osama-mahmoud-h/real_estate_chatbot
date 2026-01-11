package semsem.chatbot.orchestration.graph;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.function.Function;

/**
 * Represents an edge (connection) between nodes in the graph.
 * Supports both unconditional and conditional routing.
 */
@Data
@Builder
public class GraphEdge<S extends GraphState> {

    private String from;
    private String to;
    private boolean conditional;
    private Function<S, String> routingFunction;
    private Map<String, String> pathMap;

    public static <S extends GraphState> GraphEdge<S> unconditional(String from, String to) {
        // TODO: Implement
        return null;
    }

    public static <S extends GraphState> GraphEdge<S> conditional(
            String from,
            Function<S, String> routingFunction,
            Map<String, String> pathMap) {
        // TODO: Implement
        return null;
    }

    public String route(S state) {
        // TODO: Implement routing logic
        return null;
    }
}
