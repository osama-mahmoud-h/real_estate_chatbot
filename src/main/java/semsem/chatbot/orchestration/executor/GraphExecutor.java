package semsem.chatbot.orchestration.executor;

import reactor.core.publisher.Flux;
import semsem.chatbot.orchestration.graph.CompiledGraph;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Map;

/**
 * Executes compiled graphs with support for sync and async modes.
 */
public interface GraphExecutor {

    <S extends GraphState> S execute(CompiledGraph<S> graph, S initialState);

    <S extends GraphState> S execute(CompiledGraph<S> graph, S initialState, Map<String, Object> config);

    <S extends GraphState> Flux<S> executeStream(CompiledGraph<S> graph, S initialState);

    <S extends GraphState> Flux<S> executeStream(CompiledGraph<S> graph, S initialState, Map<String, Object> config);
}
