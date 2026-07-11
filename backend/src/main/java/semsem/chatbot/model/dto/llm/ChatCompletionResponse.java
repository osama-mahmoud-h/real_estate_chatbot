package semsem.chatbot.model.dto.llm;

import lombok.Builder;
import lombok.Data;

/**
 * Response from chat completion.
 */
@Data
@Builder
public class ChatCompletionResponse {

    private String messageId;
    private String conversationId;
    private String content;
    private String role;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private long latencyMs;
    private String model;
    private String provider;
    private String finishReason;
}
