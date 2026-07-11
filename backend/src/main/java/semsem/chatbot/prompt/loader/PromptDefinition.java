package semsem.chatbot.prompt.loader;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Represents a loaded prompt definition from file.
 * Supports both simple prompts and chat-style prompts with multiple messages.
 */
@Data
@Builder
public class PromptDefinition {

    private String name;
    private String description;
    private String version;

    // For simple single prompts
    private String template;

    // For chat-style prompts
    private String systemPrompt;
    private String userPrompt;
    private String assistantPrompt;

    // For multi-message prompts
    private List<PromptMessage> messages;

    // Input variables expected by this prompt
    private List<String> inputVariables;

    // Optional examples for few-shot prompts
    private List<PromptExample> examples;

    // Metadata
    private Map<String, Object> metadata;

    @Data
    @Builder
    public static class PromptMessage {
        private String role;
        private String content;
    }

    @Data
    @Builder
    public static class PromptExample {
        private String input;
        private String output;
    }

    /**
     * Check if this is a chat-style prompt.
     */
    public boolean isChatPrompt() {
        return (messages != null && !messages.isEmpty())
                || systemPrompt != null
                || userPrompt != null;
    }

    /**
     * Check if this is a simple template prompt.
     */
    public boolean isSimplePrompt() {
        return template != null && !template.isEmpty();
    }
}
