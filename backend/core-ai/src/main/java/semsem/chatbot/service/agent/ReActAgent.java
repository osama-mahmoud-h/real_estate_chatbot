package semsem.chatbot.service.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * ReAct (Reasoning + Acting) agent implementation.
 * Uses thought-action-observation loop for problem solving.
 */
@RequiredArgsConstructor
@Builder
public class ReActAgent extends BaseAgent {

    private final LLMService llmService;
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
