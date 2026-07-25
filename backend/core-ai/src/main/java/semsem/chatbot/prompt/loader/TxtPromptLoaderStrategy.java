package semsem.chatbot.prompt.loader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Strategy for loading prompts from plain text files.
 * Supports simple format with optional metadata in comments.
 */
@Slf4j
@Component
public class TxtPromptLoaderStrategy implements PromptLoaderStrategy {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("txt", "prompt");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");
    private static final Pattern METADATA_PATTERN = Pattern.compile("^#\\s*(\\w+):\\s*(.+)$");

    @Override
    public PromptDefinition load(Path filePath) throws IOException {
        log.debug("Loading TXT prompt from: {}", filePath);
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        PromptDefinition definition = loadFromString(content);
        if (definition.getName() == null) {
            definition.setName(getNameFromPath(filePath));
        }
        return definition;
    }

    @Override
    public PromptDefinition load(InputStream inputStream) throws IOException {
        String content;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            content = reader.lines().collect(Collectors.joining("\n"));
        }
        return loadFromString(content);
    }

    @Override
    public PromptDefinition loadFromString(String content) {
        PromptDefinition.PromptDefinitionBuilder builder = PromptDefinition.builder();

        StringBuilder templateBuilder = new StringBuilder();
        List<String> inputVariables = new ArrayList<>();

        String[] lines = content.split("\n");
        for (String line : lines) {
            // Check for metadata comments
            Matcher metaMatcher = METADATA_PATTERN.matcher(line.trim());
            if (metaMatcher.matches()) {
                String key = metaMatcher.group(1).toLowerCase();
                String value = metaMatcher.group(2).trim();
                switch (key) {
                    case "name" -> builder.name(value);
                    case "description" -> builder.description(value);
                    case "version" -> builder.version(value);
                }
            } else if (!line.trim().startsWith("#")) {
                // Non-comment line is part of template
                templateBuilder.append(line).append("\n");
            }
        }

        String template = templateBuilder.toString().trim();
        builder.template(template);

        // Extract input variables from template
        Matcher varMatcher = VARIABLE_PATTERN.matcher(template);
        while (varMatcher.find()) {
            String varName = varMatcher.group(1);
            if (!inputVariables.contains(varName)) {
                inputVariables.add(varName);
            }
        }
        builder.inputVariables(inputVariables);

        return builder.build();
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    private String getNameFromPath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
