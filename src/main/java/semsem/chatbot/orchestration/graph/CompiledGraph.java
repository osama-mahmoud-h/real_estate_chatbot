package semsem.chatbot.orchestration.graph;

import reactor.core.publisher.Flux;
import semsem.chatbot.orchestration.checkpointer.Checkpointer;

import java.util.Map;

/**
 * Compiled executable graph.
 * Executes the workflow defined by StateGraph.
 */
public class CompiledGraph<S extends GraphState> {

    private final Map<String, ?> nodes;
    private final Map<String, ?> edges;
    private final String entryPoint;
    private final Checkpointer checkpointer;

    public CompiledGraph(Map<String, ?> nodes, Map<String, ?> edges,
                         String entryPoint, Checkpointer checkpointer) {
        this.nodes = nodes;
        this.edges = edges;
        this.entryPoint = entryPoint;
        this.checkpointer = checkpointer;
    }

    public S invoke(S initialState) {
        // TODO: Implement synchronous execution
        return null;
    }

    public S invoke(S initialState, Map<String, Object> config) {
        // TODO: Implement with config
        return null;
    }

    public Flux<S> stream(S initialState) {
        // TODO: Implement streaming execution
        return Flux.empty();
    }

    public Flux<S> stream(S initialState, Map<String, Object> config) {
        // TODO: Implement streaming with config
        return Flux.empty();
    }
}
