package semsem.chatbot.memory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base class for conversation memory implementations.
 */
@Slf4j
@Getter
@Setter
public abstract class ConversationMemory implements MemoryService {

    protected String conversationId;
    protected List<Map<String, String>> messages = new ArrayList<>();
    protected int maxMessages = 100;

    @Override
    public void addMessage(String conversationId, String role, String content) {
        if (role == null || content == null) {
            log.warn("Ignoring memory message with null role or content for conversationId={}", conversationId);
            return;
        }
        try {
            this.conversationId = conversationId;
            messages.add(Map.of("role", role, "content", content));
            if (messages.size() > maxMessages) {
                messages.remove(0);
                log.debug("Evicted oldest message from memory buffer for conversationId={} (maxMessages={})",
                        conversationId, maxMessages);
            }
            log.debug("Added {} message to memory for conversationId={} (currentSize={})",
                    role, conversationId, messages.size());
        } catch (Exception ex) {
            log.error("Failed to add message to memory for conversationId={}: {}",
                    conversationId, ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, String>> getMessages(String conversationId) {
        return new ArrayList<>(messages);
    }

    @Override
    public List<Map<String, String>> getMessages(String conversationId, int limit) {
        if (limit <= 0) {
            log.warn("Requested memory messages with non-positive limit={} for conversationId={}",
                    limit, conversationId);
            return Collections.emptyList();
        }
        int start = Math.max(0, messages.size() - limit);
        return new ArrayList<>(messages.subList(start, messages.size()));
    }

    @Override
    public void clear(String conversationId) {
        int count = messages.size();
        messages.clear();
        log.info("Cleared {} message(s) from memory for conversationId={}", count, conversationId);
    }

    @Override
    public abstract String getFormattedHistory(String conversationId);
}
