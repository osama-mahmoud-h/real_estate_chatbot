package semsem.chatbot.chain;

import java.util.List;
import java.util.Map;

/**
 * Base interface for chain memory.
 * Provides conversation history and context storage for stateful chains.
 */
public interface ChainMemory {

    // ==================== History ====================

    /**
     * Returns the conversation history.
     */
    List<Map<String, String>> getHistory();

    /**
     * Adds a message to the conversation history.
     *
     * @param role    the message role (user, assistant, system)
     * @param content the message content
     */
    void addMessage(String role, String content);

    /**
     * Clears the conversation history.
     */
    void clear();

    // ==================== Context Variables ====================

    /**
     * Returns stored context variables.
     */
    Map<String, Object> getContext();

    /**
     * Stores a context variable.
     */
    void setContext(String key, Object value);

    // ==================== Buffer ====================

    /**
     * Returns the memory buffer as a formatted string for prompt injection.
     */
    String getBufferString();

    /**
     * Returns the maximum number of messages to retain.
     */
    int getMaxMessages();
}