package semsem.chatbot.service.llm.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Request object for LLM calls.
 */
@Data
@Builder
public class LLMRequest {

    private String prompt;
    private List<Map<String, String>> messages;
    private String systemPrompt;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private List<String> stopSequences;
    private Map<String, Object> metadata;

    // Tool/Function calling
    private List<ToolDefinition> tools;
    private String toolChoice; // "auto", "none", or specific tool name

    @Data
    @Builder
    public static class ToolDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;
    }
}
