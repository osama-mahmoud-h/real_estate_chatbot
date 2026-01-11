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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultGraphExecutor implements GraphExecutor {

    @Override
    public <S extends GraphState> S execute(CompiledGraph<S> graph, S initialState) {
        // TODO: Implement
        return null;
    }

    @Override
    public <S extends GraphState> S execute(CompiledGraph<S> graph, S initialState, Map<String, Object> config) {
        // TODO: Implement
        return null;
    }

    @Override
    public <S extends GraphState> Flux<S> executeStream(CompiledGraph<S> graph, S initialState) {
        // TODO: Implement
        return Flux.empty();
    }

    @Override
    public <S extends GraphState> Flux<S> executeStream(CompiledGraph<S> graph, S initialState, Map<String, Object> config) {
        // TODO: Implement
        return Flux.empty();
    }
}
