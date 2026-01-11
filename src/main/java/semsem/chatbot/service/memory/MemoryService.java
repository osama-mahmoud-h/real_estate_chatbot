package semsem.chatbot.service.memory;

import java.util.List;
import java.util.Map;

/**
 * Interface for conversation memory management.
 */
public interface MemoryService {

    void addMessage(String conversationId, String role, String content);

    List<Map<String, String>> getMessages(String conversationId);

    List<Map<String, String>> getMessages(String conversationId, int limit);

    String getFormattedHistory(String conversationId);

    void clear(String conversationId);

    void save(String conversationId);

    void load(String conversationId);
}
