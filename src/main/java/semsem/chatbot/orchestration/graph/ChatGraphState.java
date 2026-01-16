package semsem.chatbot.orchestration.graph;

import lombok.Builder;
import lombok.Data;
import semsem.chatbot.orchestration.graph.output.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat-specific implementation of GraphState.
 * Contains conversation context, messages, and typed node outputs.
 * Each node writes to its specific typed output field.
 */
@Data
@Builder
public class ChatGraphState implements GraphState {

    // ==================== Input Fields ====================
    private String conversationId;
    private String userQuery;

    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    // ==================== Node Outputs (Typed) ====================
    private LanguageDetectorOutput languageDetectorOutput;
    private IntentClassifierOutput intentClassifierOutput;
    private EntityExtractorOutput entityExtractorOutput;
    private RagRetrieverOutput ragRetrieverOutput;
    private SqlGeneratorOutput sqlGeneratorOutput;
    private SqlExecutorOutput sqlExecutorOutput;
    private ResponseGeneratorOutput responseGeneratorOutput;
    private FinalResponseBuilderOutput finalResponseBuilderOutput;

    // ==================== Final Response ====================
    private String response;

    // ==================== Metadata ====================
    @Builder.Default
    private StateMetadata metadata = StateMetadata.builder().build();

    @Override
    public void addMessage(ChatMessage message) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(message);
    }

    /**
     * Create initial state from user query.
     */
    public static ChatGraphState fromQuery(String conversationId, String query) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.user(query));

        return ChatGraphState.builder()
                .conversationId(conversationId)
                .userQuery(query)
                .messages(messages)
                .metadata(StateMetadata.builder().build())
                .build();
    }

    /**
     * Create initial state with thread context.
     */
    public static ChatGraphState withContext(String conversationId, String query,
                                              String threadId, String userId) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.user(query));

        return ChatGraphState.builder()
                .conversationId(conversationId)
                .userQuery(query)
                .messages(messages)
                .metadata(StateMetadata.builder()
                        .threadId(threadId)
                        .userId(userId)
                        .build())
                .build();
    }
}
