package semsem.chatbot.orchestration.graph;

import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.checkpointer.Checkpointer;
import semsem.chatbot.orchestration.node.GraphNode;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * LangGraph-style state graph builder.
 * Defines nodes, edges, and conditional routing for workflow execution.
 * Uses GraphNodeNames enum for type-safe node references.
 */
public class StateGraph<S extends GraphState> {

    private final Map<GraphNodeNames, GraphNode<S>> nodes = new EnumMap<>(GraphNodeNames.class);
    private final Map<GraphNodeNames, GraphEdge<S>> edges = new EnumMap<>(GraphNodeNames.class);
    private GraphNodeNames entryPoint;
    private GraphNodeNames finishPoint;

    public StateGraph<S> addNode(GraphNodeNames name, GraphNode<S> node) {
        nodes.put(name, node);
        return this;
    }

    public StateGraph<S> addEdge(GraphNodeNames from, GraphNodeNames to) {
        edges.put(from, GraphEdge.unconditional(from, to));
        return this;
    }

    public StateGraph<S> addConditionalEdge(
            GraphNodeNames from,
            Function<S, GraphNodeNames> routingFunction,
            Map<GraphNodeNames, GraphNodeNames> pathMap) {
        edges.put(from, GraphEdge.conditional(from, routingFunction, pathMap));
        return this;
    }

    public StateGraph<S> setEntryPoint(GraphNodeNames nodeName) {
        this.entryPoint = nodeName;
        return this;
    }

    public StateGraph<S> setFinishPoint(GraphNodeNames nodeName) {
        this.finishPoint = nodeName;
        return this;
    }

    public CompiledGraph<S> compile() {
        return compile(null);
    }

    public CompiledGraph<S> compile(Checkpointer checkpointer) {
        validateGraph();
        return new CompiledGraph<>(nodes, edges, entryPoint, finishPoint, checkpointer);
    }

    private void validateGraph() {
        if (entryPoint == null) {
            throw new IllegalStateException("Entry point must be set");
        }
        if (!nodes.containsKey(entryPoint)) {
            throw new IllegalStateException("Entry point node not found: " + entryPoint);
        }
        // Validate all edge targets exist
        for (Map.Entry<GraphNodeNames, GraphEdge<S>> entry : edges.entrySet()) {
            GraphEdge<S> edge = entry.getValue();
            if (!edge.isConditional() && !nodes.containsKey(edge.getTo())) {
                throw new IllegalStateException("Edge target node not found: " + edge.getTo());
            }
        }
    }

    // Getters for CompiledGraph construction
    public Map<GraphNodeNames, GraphNode<S>> getNodes() {
        return nodes;
    }

    public Map<GraphNodeNames, GraphEdge<S>> getEdges() {
        return edges;
    }

    public GraphNodeNames getEntryPoint() {
        return entryPoint;
    }

    public GraphNodeNames getFinishPoint() {
        return finishPoint;
    }
}
