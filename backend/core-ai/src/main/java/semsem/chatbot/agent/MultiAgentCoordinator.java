package semsem.chatbot.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.llm.gateway.LLMGateway;

import java.util.List;
import java.util.Map;

/**
 * Coordinates multiple agents for complex tasks.
 * Routes tasks to appropriate agents and aggregates results.
 */
@RequiredArgsConstructor
@Builder
public class MultiAgentCoordinator extends BaseAgent {

    private final LLMGateway llmGateway;
    private final List<Agent> agents;
    private final String routerPrompt;

    @Override
    public String getName() {
        return "MultiAgentCoordinator";
    }

    @Override
    protected AgentResponse executeLoop(String input, Map<String, Object> context) {
        // TODO: Implement multi-agent coordination
        return AgentResponse.success("", null);
    }

    protected Agent selectAgent(String input) {
        // TODO: Use LLM to select appropriate agent
        return null;
    }
}
