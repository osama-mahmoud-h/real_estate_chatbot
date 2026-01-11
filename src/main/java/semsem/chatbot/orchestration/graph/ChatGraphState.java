package semsem.chatbot.orchestration.graph;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat-specific implementation of GraphState.
 * Contains conversation context, messages, and RAG-related data.
 */
@Data
@Builder
public class ChatGraphState implements GraphState {

    private String conversationId;
    private String userQuery;
    private String response;
    private List<String> retrievedDocuments;
    private List<Map<String, Object>> messages;
    private Map<String, Object> metadata;

    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

    @Override
    public Map<String, Object> getData() {
        // TODO: Implement
        return data;
    }

    @Override
    public GraphState withData(String key, Object value) {
        // TODO: Implement
        return this;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        // TODO: Implement
        return null;
    }

    @Override
    public GraphState merge(GraphState other) {
        // TODO: Implement
        return this;
    }
}
