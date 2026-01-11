package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.function.Function;

/**
 * Node that evaluates conditions for routing decisions.
 */
@RequiredArgsConstructor
public class ConditionalNode<S extends GraphState> implements GraphNode<S> {

    private final String name;
    private final Function<S, String> conditionEvaluator;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement condition evaluation
        return state;
    }

    public String evaluateCondition(S state) {
        return conditionEvaluator.apply(state);
    }
}
