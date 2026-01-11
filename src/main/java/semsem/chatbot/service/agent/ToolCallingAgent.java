package semsem.chatbot.service.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Agent specialized for function/tool calling.
 * Uses structured tool definitions for reliable execution.
 */
@RequiredArgsConstructor
@Builder
public class ToolCallingAgent extends BaseAgent {

    private final LLMService llmService;
    private final boolean parallelToolCalls;

    @Override
    public String getName() {
        return "ToolCallingAgent";
    }

    @Override
    protected AgentResponse executeLoop(String input, Map<String, Object> context) {
        // TODO: Implement tool calling with structured outputs
        return AgentResponse.success("", null);
    }
}
