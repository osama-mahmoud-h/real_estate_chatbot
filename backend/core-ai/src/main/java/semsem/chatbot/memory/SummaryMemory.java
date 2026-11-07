package semsem.chatbot.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.llm.gateway.LLMGateway;

/**
 * Memory that maintains a running summary of the conversation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryMemory extends ConversationMemory {

    private final LLMGateway llmGateway;
    private String currentSummary = "";
    private int summaryThreshold = 10; // Summarize after N messages

    @Override
    public String getFormattedHistory(String conversationId) {
        // Return summary + recent messages
        StringBuilder sb = new StringBuilder();
        if (!currentSummary.isEmpty()) {
            sb.append("Previous conversation summary:\n").append(currentSummary).append("\n\n");
        }
        sb.append("Recent messages:\n");
        getMessages(conversationId, 5).forEach(m ->
                sb.append(m.get("role")).append(": ").append(m.get("content")).append("\n")
        );
        return sb.toString();
    }

    public void updateSummary() {
        // TODO: Use LLM to create running summary
    }

    @Override
    public void save(String conversationId) {
        // TODO: Persist summary
    }

    @Override
    public void load(String conversationId) {
        // TODO: Load summary
    }
}
