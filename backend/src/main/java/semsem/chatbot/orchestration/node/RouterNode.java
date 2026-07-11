package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Node that routes to different paths based on LLM classification.
 */
@RequiredArgsConstructor
public class RouterNode<S extends GraphState> implements GraphNode<S> {

    private final GraphNodeNames name;
    private final LLMService llmService;
    private final Map<GraphNodeNames, String> routeDescriptions;

    @Override
    public GraphNodeNames getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement LLM-based routing
        return state;
    }

    public GraphNodeNames determineRoute(S state) {
        // TODO: Use LLM to classify and route
        return null;
    }
}
