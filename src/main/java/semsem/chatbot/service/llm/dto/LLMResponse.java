package semsem.chatbot.service.llm.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Response object from LLM calls.
 */
@Data
@Builder
public class LLMResponse {

    private String content;
    private String finishReason;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private long latencyMs;
    private String model;
    private String provider;

    // Tool/Function call results
    private List<ToolCall> toolCalls;
    private Map<String, Object> metadata;

    @Data
    @Builder
    public static class ToolCall {
        private String id;
        private String name;
        private Map<String, Object> arguments;
    }
}
