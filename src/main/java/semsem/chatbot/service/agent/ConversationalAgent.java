package semsem.chatbot.service.agent;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;
import semsem.chatbot.service.memory.MemoryService;

import java.util.Map;

/**
 * Conversational agent with memory support.
 * Maintains conversation context across interactions.
 */
@RequiredArgsConstructor
@Builder
public class ConversationalAgent extends BaseAgent {

    private final LLMService llmService;
    private final MemoryService memoryService;
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
