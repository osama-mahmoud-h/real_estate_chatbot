package semsem.chatbot.orchestration.graph;

import lombok.Builder;
import lombok.Data;
import semsem.chatbot.model.enums.GraphNodeNames;

import java.util.Map;
import java.util.function.Function;

/**
 * Represents an edge (connection) between nodes in the graph.
 * Supports both unconditional and conditional routing.
 * Uses GraphNodeNames enum for type-safe node references.
 */
@Data
@Builder
public class GraphEdge<S extends GraphState> {

    private GraphNodeNames from;
    private GraphNodeNames to;
    private boolean conditional;
    private Function<S, GraphNodeNames> routingFunction;
    private Map<GraphNodeNames, GraphNodeNames> pathMap;

    public static <S extends GraphState> GraphEdge<S> unconditional(GraphNodeNames from, GraphNodeNames to) {
        return GraphEdge.<S>builder()
                .from(from)
                .to(to)
                .conditional(false)
                .build();
    }

    public static <S extends GraphState> GraphEdge<S> conditional(
            GraphNodeNames from,
            Function<S, GraphNodeNames> routingFunction,
            Map<GraphNodeNames, GraphNodeNames> pathMap) {
        return GraphEdge.<S>builder()
                .from(from)
                .conditional(true)
                .routingFunction(routingFunction)
                .pathMap(pathMap)
                .build();
    }

    public GraphNodeNames route(S state) {
        if (!conditional) {
            return to;
        }
        if (routingFunction != null) {
            GraphNodeNames routeResult = routingFunction.apply(state);
            if (pathMap != null && pathMap.containsKey(routeResult)) {
                return pathMap.get(routeResult);
            }
            return routeResult;
        }
        return to;
    }
}
