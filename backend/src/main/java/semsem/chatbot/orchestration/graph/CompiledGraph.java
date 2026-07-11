package semsem.chatbot.orchestration.graph;

import lombok.Getter;
import reactor.core.publisher.Flux;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.checkpointer.Checkpointer;
import semsem.chatbot.orchestration.node.GraphNode;

import java.util.Map;

/**
 * Compiled executable graph.
 * Executes the workflow defined by StateGraph.
 * Uses GraphNodeNames enum for type-safe node references.
 */
@Getter
public class CompiledGraph<S extends GraphState> {

    private final Map<GraphNodeNames, GraphNode<S>> nodes;
    private final Map<GraphNodeNames, GraphEdge<S>> edges;
    private final GraphNodeNames entryPoint;
    private final GraphNodeNames finishPoint;
    private final Checkpointer checkpointer;

    public CompiledGraph(Map<GraphNodeNames, GraphNode<S>> nodes,
                         Map<GraphNodeNames, GraphEdge<S>> edges,
                         GraphNodeNames entryPoint,
                         GraphNodeNames finishPoint,
                         Checkpointer checkpointer) {
        this.nodes = nodes;
        this.edges = edges;
        this.entryPoint = entryPoint;
        this.finishPoint = finishPoint;
        this.checkpointer = checkpointer;
    }

    public S invoke(S initialState) {
        return invoke(initialState, null);
    }

    public S invoke(S initialState, Map<String, Object> config) {
        S currentState = initialState;
        GraphNodeNames currentNode = entryPoint;

        while (currentNode != null) {
            // Execute current node
            GraphNode<S> node = nodes.get(currentNode);
            if (node == null) {
                throw new IllegalStateException("Node not found: " + currentNode);
            }

            currentState = node.execute(currentState);

            // Save checkpoint if available
            if (checkpointer != null && currentState.getMetadata() != null) {
                String threadId = currentState.getMetadata().getThreadId();
                if (threadId != null) {
                    checkpointer.save(threadId, currentNode, currentState);
                }
            }

            // Check if we reached finish point
            if (currentNode == finishPoint) {
                break;
            }

            // Get next node from edge
            GraphEdge<S> edge = edges.get(currentNode);
            if (edge == null) {
                break; // No outgoing edge, stop execution
            }

            currentNode = edge.route(currentState);
        }

        return currentState;
    }

    public Flux<S> stream(S initialState) {
        return stream(initialState, null);
    }

    public Flux<S> stream(S initialState, Map<String, Object> config) {
        return Flux.create(sink -> {
            S currentState = initialState;
            GraphNodeNames currentNode = entryPoint;

            while (currentNode != null) {
                // Execute current node
                GraphNode<S> node = nodes.get(currentNode);
                if (node == null) {
                    sink.error(new IllegalStateException("Node not found: " + currentNode));
                    return;
                }

                currentState = node.execute(currentState);
                sink.next(currentState); // Emit state after each node

                // Save checkpoint if available
                if (checkpointer != null && currentState.getMetadata() != null) {
                    String threadId = currentState.getMetadata().getThreadId();
                    if (threadId != null) {
                        checkpointer.save(threadId, currentNode, currentState);
                    }
                }

                // Check if we reached finish point
                if (currentNode == finishPoint) {
                    break;
                }

                // Get next node from edge
                GraphEdge<S> edge = edges.get(currentNode);
                if (edge == null) {
                    break;
                }

                currentNode = edge.route(currentState);
            }

            sink.complete();
        });
    }
}
