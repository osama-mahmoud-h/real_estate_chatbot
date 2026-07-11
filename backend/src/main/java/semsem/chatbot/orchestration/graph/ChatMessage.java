package semsem.chatbot.orchestration.graph;

import lombok.Builder;
import lombok.Data;
import semsem.chatbot.model.enums.MessageRole;

import java.time.Instant;
import java.util.Map;

/**
 * Lightweight message representation for graph state.
 * Used during workflow execution, distinct from the persisted Message entity.
 */
@Data
@Builder
public class ChatMessage {

    private MessageRole role;
    private String content;

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String toolCallId;
    private String functionName;
    private Map<String, Object> functionArguments;

    public static ChatMessage user(String content) {
        return ChatMessage.builder()
                .role(MessageRole.USER)
                .content(content)
                .build();
    }

    public static ChatMessage assistant(String content) {
        return ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content(content)
                .build();
    }

    public static ChatMessage system(String content) {
        return ChatMessage.builder()
                .role(MessageRole.SYSTEM)
                .content(content)
                .build();
    }

    public static ChatMessage toolResult(String toolCallId, String content) {
        return ChatMessage.builder()
                .role(MessageRole.TOOL)
                .toolCallId(toolCallId)
                .content(content)
                .build();
    }

    public static ChatMessage functionCall(String functionName, Map<String, Object> arguments) {
        return ChatMessage.builder()
                .role(MessageRole.FUNCTION)
                .functionName(functionName)
                .functionArguments(arguments)
                .build();
    }
}