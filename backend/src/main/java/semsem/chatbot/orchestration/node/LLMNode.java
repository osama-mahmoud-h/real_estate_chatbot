package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.service.llm.LLMService;

/**
 * Node that invokes an LLM for text generation.
 */
@RequiredArgsConstructor
public class LLMNode<S extends GraphState> implements GraphNode<S> {

    private final GraphNodeNames name;
    private final LLMService llmService;
    private final String promptTemplate;

    @Override
    public GraphNodeNames getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement LLM invocation
        return state;
    }
}
