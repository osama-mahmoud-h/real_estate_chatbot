package semsem.chatbot.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.llm.gateway.LLMGateway;

import java.util.Map;

/**
 * Conversational agent with memory support.
 * Maintains conversation context across interactions.
 */
@RequiredArgsConstructor
@Builder
public class ConversationalAgent extends BaseAgent {

    private final LLMGateway llmGateway;
    private final String conversationPrompt;

    @Override
    public String getName() {
        return "ConversationalAgent";
    }

    @Override
    protected AgentResponse executeLoop(String input, Map<String, Object> context) {
        // TODO: Implement conversational agent with memory
        return AgentResponse.success("", null);
    }
}
