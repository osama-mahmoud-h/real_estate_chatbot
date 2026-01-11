package semsem.chatbot.service.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory short-term conversation buffer.
 * Keeps last N messages in memory.
 */
@Slf4j
@Service
public class ShortTermMemory extends ConversationMemory {

    private final Map<String, ConversationMemory> conversationBuffers = new ConcurrentHashMap<>();

    public ShortTermMemory() {
        this.maxMessages = 20;
    }

    @Override
    public String getFormattedHistory(String conversationId) {
        return getMessages(conversationId).stream()
                .map(m -> m.get("role") + ": " + m.get("content"))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void save(String conversationId) {
        // In-memory, no persistence needed
        log.debug("Short-term memory does not persist: {}", conversationId);
    }

    @Override
    public void load(String conversationId) {
        // In-memory, no loading needed
        log.debug("Short-term memory loaded for: {}", conversationId);
    }
}
