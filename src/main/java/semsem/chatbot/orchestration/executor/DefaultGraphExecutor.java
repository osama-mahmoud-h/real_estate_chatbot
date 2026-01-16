package semsem.chatbot.orchestration.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import semsem.chatbot.orchestration.graph.CompiledGraph;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Map;

/**
 * Default implementation of GraphExecutor.
 * Delegates execution to CompiledGraph which handles the actual workflow.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultGraphExecutor implements GraphExecutor {

    @Override
    public <S extends GraphState> S execute(CompiledGraph<S> graph, S initialState) {
        log.debug("Executing graph starting from {}", graph.getEntryPoint());
        return graph.invoke(initialState);
    }

    @Override
    public <S extends GraphState> S execute(CompiledGraph<S> graph, S initialState, Map<String, Object> config) {
        log.debug("Executing graph with config starting from {}", graph.getEntryPoint());
        return graph.invoke(initialState, config);
    }

    @Override
    public <S extends GraphState> Flux<S> executeStream(CompiledGraph<S> graph, S initialState) {
        log.debug("Streaming graph execution starting from {}", graph.getEntryPoint());
        return graph.stream(initialState);
    }

    @Override
    public <S extends GraphState> Flux<S> executeStream(CompiledGraph<S> graph, S initialState, Map<String, Object> config) {
        log.debug("Streaming graph execution with config starting from {}", graph.getEntryPoint());
        return graph.stream(initialState, config);
    }
}
