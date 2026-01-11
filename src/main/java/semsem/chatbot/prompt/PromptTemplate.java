package semsem.chatbot.prompt;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template for prompts with variable substitution.
 */
public class PromptTemplate {

    private final String template;
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    public PromptTemplate(String template) {
        this.template = template;
    }

    public String format(Map<String, Object> variables) {
        String result = template;
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String varName = matcher.group(1);
            Object value = variables.get(varName);
            if (value != null) {
                result = result.replace("{{" + varName + "}}", value.toString());
            }
        }
        return result;
    }

    public static PromptTemplate fromString(String template) {
        return new PromptTemplate(template);
    }

    public String getTemplate() {
        return template;
    }
}
