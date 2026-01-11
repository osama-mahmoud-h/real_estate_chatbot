package semsem.chatbot.service.memory;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for conversation memory implementations.
 */
@Getter
@Setter
public abstract class ConversationMemory implements MemoryService {

    protected String conversationId;
    protected List<Map<String, String>> messages = new ArrayList<>();
    protected int maxMessages = 100;

    @Override
    public void addMessage(String conversationId, String role, String content) {
        this.conversationId = conversationId;
        messages.add(Map.of("role", role, "content", content));
        if (messages.size() > maxMessages) {
            messages.remove(0);
        }
    }

    @Override
    public List<Map<String, String>> getMessages(String conversationId) {
        return new ArrayList<>(messages);
    }

    @Override
    public List<Map<String, String>> getMessages(String conversationId, int limit) {
        int start = Math.max(0, messages.size() - limit);
        return new ArrayList<>(messages.subList(start, messages.size()));
    }

    @Override
    public void clear(String conversationId) {
        messages.clear();
    }

    @Override
    public abstract String getFormattedHistory(String conversationId);
}
