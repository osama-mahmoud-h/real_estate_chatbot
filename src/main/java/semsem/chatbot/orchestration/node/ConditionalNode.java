package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.function.Function;

/**
 * Node that evaluates conditions for routing decisions.
 */
@RequiredArgsConstructor
public class ConditionalNode<S extends GraphState> implements GraphNode<S> {

    private final GraphNodeNames name;
    private final Function<S, GraphNodeNames> conditionEvaluator;

    @Override
    public GraphNodeNames getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // Conditional nodes don't modify state, they just evaluate conditions
        return state;
    }

    public GraphNodeNames evaluateCondition(S state) {
        return conditionEvaluator.apply(state);
    }
}

