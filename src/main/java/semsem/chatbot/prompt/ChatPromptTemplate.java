package semsem.chatbot.prompt;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Template for chat-style prompts with system, user, and assistant messages.
 */
@Data
@Builder
public class ChatPromptTemplate {

    private String systemTemplate;
    private String userTemplate;
    private String assistantTemplate;

    public List<Map<String, String>> format(Map<String, Object> variables) {
        List<Map<String, String>> messages = new ArrayList<>();

        if (systemTemplate != null && !systemTemplate.isEmpty()) {
            messages.add(Map.of(
                    "role", "system",
                    "content", new PromptTemplate(systemTemplate).format(variables)
            ));
        }

        if (userTemplate != null && !userTemplate.isEmpty()) {
            messages.add(Map.of(
                    "role", "user",
                    "content", new PromptTemplate(userTemplate).format(variables)
            ));
        }

        if (assistantTemplate != null && !assistantTemplate.isEmpty()) {
            messages.add(Map.of(
                    "role", "assistant",
                    "content", new PromptTemplate(assistantTemplate).format(variables)
            ));
        }

        return messages;
    }

    public static ChatPromptTemplate fromMessages(String systemPrompt, String userPrompt) {
        return ChatPromptTemplate.builder()
                .systemTemplate(systemPrompt)
                .userTemplate(userPrompt)
                .build();
    }
}
