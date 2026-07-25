package semsem.chatbot.prompt.builder;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import semsem.chatbot.prompt.PromptTemplate;
import semsem.chatbot.prompt.loader.PromptDefinition;
import semsem.chatbot.prompt.loader.PromptDefinitionsLoader;
import semsem.chatbot.prompt.loader.PromptRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatPromptFactory {

    private final PromptRegistry registry;
    private final PromptDefinitionsLoader definitions;

    public Spec forPrompt(String promptName) {
        return new Spec(promptName);
    }

    @RequiredArgsConstructor
    public class Spec {

        private final String promptName;
        private final Map<String, Object> variables = new HashMap<>();
        private final List<Message> history = new ArrayList<>();

        public Spec var(String name, Object value) {
            variables.put(name, value);
            return this;
        }

        public Spec vars(Map<String, Object> values) {
            variables.putAll(values);
            return this;
        }

        // Binds the section under its snake_case variable name: "intent-definitions" -> intent_definitions
        public Spec section(String sectionName) {
            return section(sectionName.replace('-', '_'), sectionName);
        }

        public Spec section(String varName, String sectionName) {
            variables.put(varName, definitions.get(promptName, sectionName));
            return this;
        }

        public Spec sections(String... sectionNames) {
            for (String sectionName : sectionNames) {
                section(sectionName);
            }
            return this;
        }

        public Spec history(List<Message> messages) {
            if (messages != null) {
                history.addAll(messages);
            }
            return this;
        }

        public Prompt build() {
            PromptDefinition definition = registry.getOrThrow(promptName);

            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(format(definition.getSystemPrompt())));
            messages.addAll(history);
            messages.add(new UserMessage(format(definition.getUserPrompt())));

            return new Prompt(messages);
        }

        private String format(String template) {
            return new PromptTemplate(template).format(variables);
        }
    }
}