package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.service.llm.LLMService;

/**
 * Node that invokes an LLM for text generation.
 */
@RequiredArgsConstructor
public class LLMNode<S extends GraphState> implements GraphNode<S> {

    private final String name;
    private final LLMService llmService;
    private final String promptTemplate;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement LLM invocation
        return state;
    }
}
