package semsem.chatbot.prompt.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads prompt definition files (examples, types, patterns) from the classpath
 * and provides them as formatted strings for prompt template injection.
 */
@Slf4j
@Component
public class PromptDefinitionsLoader {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper yamlMapper;

    @Value("${chatbot.prompts.definitions-path:classpath:prompts/workflow/definitions}")
    private String definitionsBasePath;

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public PromptDefinitionsLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    @PostConstruct
    public void init() {
        loadAllDefinitions();
    }

    /**
     * Get a loaded definition content by its key (e.g., "query-analyzer/examples").
     */
    public String get(String promptName, String definitionName) {
        String key = promptName + "/" + definitionName;
        String content = cache.get(key);
        if (content == null) {
            log.warn("Definition not found: {}", key);
            return "";
        }
        return content;
    }

    private void loadAllDefinitions() {
        String[][] definitions = {
                {"query-analyzer", "intent-definitions"},
                {"query-analyzer", "entity-types"},
                {"query-analyzer", "examples"},
                {"sql-generator", "enum-definitions"},
                {"sql-generator", "query-patterns"},
                {"sql-generator", "examples"},
                {"response-generator", "response-patterns"},
                {"response-generator", "examples"}
        };

        for (String[] def : definitions) {
            loadDefinition(def[0], def[1]);
        }

        log.info("Loaded {} prompt definitions", cache.size());
    }

    private void loadDefinition(String promptName, String definitionName) {
        String key = promptName + "/" + definitionName;
        String resourcePath = definitionsBasePath + "/" + promptName + "/" + definitionName + ".yaml";

        try {
            Resource resource = resourceLoader.getResource(resourcePath);
            if (resource.exists()) {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                cache.put(key, content);
                log.debug("Loaded definition: {}", key);
            } else {
                log.warn("Definition file not found: {}", resourcePath);
            }
        } catch (IOException e) {
            log.error("Failed to load definition {}: {}", key, e.getMessage());
        }
    }
}