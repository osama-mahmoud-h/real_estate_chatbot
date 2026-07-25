package semsem.chatbot.prompt.builder;

import semsem.chatbot.prompt.PromptTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for constructing prompts with fluent API.
 */
public class PromptBuilder {

    private final StringBuilder prompt = new StringBuilder();
    private final Map<String, Object> variables = new HashMap<>();

    public PromptBuilder addSection(String title, String content) {
        prompt.append("## ").append(title).append("\n");
        prompt.append(content).append("\n\n");
        return this;
    }

    public PromptBuilder addVariable(String name, Object value) {
        variables.put(name, value);
        return this;
    }

    public PromptBuilder addInstruction(String instruction) {
        prompt.append("- ").append(instruction).append("\n");
        return this;
    }

    public PromptBuilder addContext(String context) {
        prompt.append("<context>\n").append(context).append("\n</context>\n\n");
        return this;
    }

    public PromptBuilder addQuestion(String question) {
        prompt.append("<question>\n").append(question).append("\n</question>\n\n");
        return this;
    }

    public PromptBuilder addExample(String input, String output) {
        prompt.append("<example>\n");
        prompt.append("Input: ").append(input).append("\n");
        prompt.append("Output: ").append(output).append("\n");
        prompt.append("</example>\n\n");
        return this;
    }

    public String build() {
        return new PromptTemplate(prompt.toString()).format(variables);
    }

    public static PromptBuilder create() {
        return new PromptBuilder();
    }
}
