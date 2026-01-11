package semsem.chatbot.service.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.List;
import java.util.Map;

/**
 * Plan-and-Execute agent implementation.
 * First creates a plan, then executes each step.
 */
@RequiredArgsConstructor
@Builder
public class PlanAndExecuteAgent extends BaseAgent {

    private final LLMService llmService;
    private final String plannerPrompt;
    private final String executorPrompt;

    @Override
    public String getName() {
        return "PlanAndExecuteAgent";
    }

    @Override
    protected AgentResponse executeLoop(String input, Map<String, Object> context) {
        // TODO: Implement plan-and-execute pattern
        // 1. Create a plan
        // 2. Execute each step
        // 3. Re-plan if needed
        return AgentResponse.success("", null);
    }

    protected List<String> createPlan(String objective) {
        // TODO: Use LLM to create execution plan
        return List.of();
    }

    protected String executeStep(String step) {
        // TODO: Execute individual step
        return "";
    }
}
