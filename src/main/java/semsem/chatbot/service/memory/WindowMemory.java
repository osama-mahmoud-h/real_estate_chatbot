package semsem.chatbot.service.memory;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Memory with a sliding window of messages.
 */
@Slf4j
@Service
@Setter
public class WindowMemory extends ConversationMemory {

    private int windowSize = 10;

    @Override
    public String getFormattedHistory(String conversationId) {
        return getMessages(conversationId, windowSize).stream()
                .map(m -> m.get("role") + ": " + m.get("content"))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public void save(String conversationId) {
        // TODO: Implement persistence
    }

    @Override
    public void load(String conversationId) {
        // TODO: Implement loading
    }
}
