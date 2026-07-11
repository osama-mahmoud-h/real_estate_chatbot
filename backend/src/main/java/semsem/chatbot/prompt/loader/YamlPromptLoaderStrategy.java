package semsem.chatbot.prompt.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.exception.PromptLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Strategy for loading prompts from YAML files.
 */
@Slf4j
@Component
public class YamlPromptLoaderStrategy implements PromptLoaderStrategy {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("yaml", "yml");
    private final ObjectMapper yamlMapper;

    public YamlPromptLoaderStrategy() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    public PromptDefinition load(Path filePath) throws IOException {
        log.debug("Loading YAML prompt from: {}", filePath);
        try (InputStream is = Files.newInputStream(filePath)) {
            PromptDefinition definition = load(is);
            if (definition.getName() == null) {
                definition.setName(getNameFromPath(filePath));
            }
            return definition;
        }
    }

    @Override
    public PromptDefinition load(InputStream inputStream) throws IOException {
        Map<String, Object> yamlContent = yamlMapper.readValue(inputStream, Map.class);
        return parseYamlContent(yamlContent);
    }

    @Override
    public PromptDefinition loadFromString(String content) {
        try {
            Map<String, Object> yamlContent = yamlMapper.readValue(content, Map.class);
            return parseYamlContent(yamlContent);
        } catch (IOException e) {
            throw new PromptLoadException("Failed to parse YAML content", e);
        }
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    @SuppressWarnings("unchecked")
    private PromptDefinition parseYamlContent(Map<String, Object> yaml) {
        PromptDefinition.PromptDefinitionBuilder builder = PromptDefinition.builder();

        builder.name((String) yaml.get("name"));
        builder.description((String) yaml.get("description"));
        builder.version((String) yaml.get("version"));
        builder.template((String) yaml.get("template"));
        builder.systemPrompt((String) yaml.get("system_prompt"));
        builder.userPrompt((String) yaml.get("user_prompt"));
        builder.assistantPrompt((String) yaml.get("assistant_prompt"));

        if (yaml.containsKey("input_variables")) {
            builder.inputVariables((List<String>) yaml.get("input_variables"));
        }

        if (yaml.containsKey("messages")) {
            List<Map<String, String>> messagesList = (List<Map<String, String>>) yaml.get("messages");
            List<PromptDefinition.PromptMessage> messages = messagesList.stream()
                    .map(m -> PromptDefinition.PromptMessage.builder()
                            .role(m.get("role"))
                            .content(m.get("content"))
                            .build())
                    .toList();
            builder.messages(messages);
        }

        if (yaml.containsKey("examples")) {
            List<Map<String, String>> examplesList = (List<Map<String, String>>) yaml.get("examples");
            List<PromptDefinition.PromptExample> examples = examplesList.stream()
                    .map(e -> PromptDefinition.PromptExample.builder()
                            .input(e.get("input"))
                            .output(e.get("output"))
                            .build())
                    .toList();
            builder.examples(examples);
        }

        if (yaml.containsKey("metadata")) {
            builder.metadata((Map<String, Object>) yaml.get("metadata"));
        }

        return builder.build();
    }

    private String getNameFromPath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
