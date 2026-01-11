package semsem.chatbot.prompt.builder;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for few-shot prompts with examples.
 */
public class FewShotPromptBuilder {

    private String prefix = "";
    private String suffix = "";
    private String exampleSeparator = "\n\n";
    private final List<Example> examples = new ArrayList<>();

    @Data
    @Builder
    public static class Example {
        private String input;
        private String output;
    }

    public FewShotPromptBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public FewShotPromptBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public FewShotPromptBuilder separator(String separator) {
        this.exampleSeparator = separator;
        return this;
    }

    public FewShotPromptBuilder addExample(String input, String output) {
        examples.add(Example.builder().input(input).output(output).build());
        return this;
    }

    public String build(String input) {
        StringBuilder sb = new StringBuilder();

        if (!prefix.isEmpty()) {
            sb.append(prefix).append("\n\n");
        }

        for (Example example : examples) {
            sb.append("Input: ").append(example.getInput()).append("\n");
            sb.append("Output: ").append(example.getOutput());
            sb.append(exampleSeparator);
        }

        sb.append("Input: ").append(input).append("\n");
        sb.append("Output: ");

        if (!suffix.isEmpty()) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    public static FewShotPromptBuilder create() {
        return new FewShotPromptBuilder();
    }
}
