package semsem.chatbot.model.dto.llm;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Request for chat completion.
 */
@Data
@Builder
public class ChatCompletionRequest {

    private String conversationId;
    private String message;
    private String systemPrompt;
    private List<Map<String, String>> history;
    private String provider; // ollama, googlegenai, openai, anthropic
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;
}
