package semsem.chatbot.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.llm.gateway.LLMGateway;

import java.util.Map;

/**
 * ReAct (Reasoning + Acting) agent implementation.
 * Uses thought-action-observation loop for problem solving.
 */
@RequiredArgsConstructor
@Builder
public class ReActAgent extends BaseAgent {

    private final LLMGateway llmGateway;
    private final String reactPromptTemplate;

    @Override
    public String getName() {
        return "ReActAgent";
    }

    @Override
    protected AgentResponse executeLoop(String input, Map<String, Object> context) {
        // TODO: Implement ReAct loop
        // 1. Think about what to do
        // 2. Choose an action
        // 3. Execute the action
        // 4. Observe the result
        // 5. Repeat until done
        return AgentResponse.success("", null);
    }
}
