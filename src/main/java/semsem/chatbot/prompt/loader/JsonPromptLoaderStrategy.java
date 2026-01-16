package semsem.chatbot.prompt.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Strategy for loading prompts from JSON files.
 */
@Slf4j
@Component
public class JsonPromptLoaderStrategy implements PromptLoaderStrategy {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("json");
    private final ObjectMapper jsonMapper;

    public JsonPromptLoaderStrategy(ObjectMapper objectMapper) {
        this.jsonMapper = objectMapper;
    }

    @Override
    public PromptDefinition load(Path filePath) throws IOException {
        log.debug("Loading JSON prompt from: {}", filePath);
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
        Map<String, Object> jsonContent = jsonMapper.readValue(inputStream, Map.class);
        return parseJsonContent(jsonContent);
    }

    @Override
    public PromptDefinition loadFromString(String content) {
        try {
            Map<String, Object> jsonContent = jsonMapper.readValue(content, Map.class);
            return parseJsonContent(jsonContent);
        } catch (IOException e) {
            throw new PromptLoadException("Failed to parse JSON content", e);
        }
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    @SuppressWarnings("unchecked")
    private PromptDefinition parseJsonContent(Map<String, Object> json) {
        PromptDefinition.PromptDefinitionBuilder builder = PromptDefinition.builder();

        builder.name((String) json.get("name"));
        builder.description((String) json.get("description"));
        builder.version((String) json.get("version"));
        builder.template((String) json.get("template"));
        builder.systemPrompt((String) json.get("systemPrompt"));
        builder.userPrompt((String) json.get("userPrompt"));
        builder.assistantPrompt((String) json.get("assistantPrompt"));

        if (json.containsKey("inputVariables")) {
            builder.inputVariables((List<String>) json.get("inputVariables"));
        }

        if (json.containsKey("messages")) {
            List<Map<String, String>> messagesList = (List<Map<String, String>>) json.get("messages");
            List<PromptDefinition.PromptMessage> messages = messagesList.stream()
                    .map(m -> PromptDefinition.PromptMessage.builder()
                            .role(m.get("role"))
                            .content(m.get("content"))
                            .build())
                    .toList();
            builder.messages(messages);
        }

        if (json.containsKey("examples")) {
            List<Map<String, String>> examplesList = (List<Map<String, String>>) json.get("examples");
            List<PromptDefinition.PromptExample> examples = examplesList.stream()
                    .map(e -> PromptDefinition.PromptExample.builder()
                            .input(e.get("input"))
                            .output(e.get("output"))
                            .build())
                    .toList();
            builder.examples(examples);
        }

        if (json.containsKey("metadata")) {
            builder.metadata((Map<String, Object>) json.get("metadata"));
        }

        return builder.build();
    }

    private String getNameFromPath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
