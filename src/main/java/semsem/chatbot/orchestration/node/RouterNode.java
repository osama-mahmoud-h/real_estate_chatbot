package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Node that routes to different paths based on LLM classification.
 */
@RequiredArgsConstructor
public class RouterNode<S extends GraphState> implements GraphNode<S> {

    private final String name;
    private final LLMService llmService;
    private final Map<String, String> routeDescriptions;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement LLM-based routing
        return state;
    }

    public String determineRoute(S state) {
        // TODO: Use LLM to classify and route
        return null;
    }
}
