package semsem.chatbot.prompt.loader;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import semsem.chatbot.exception.PromptLoadException;
import semsem.chatbot.prompt.ChatPromptTemplate;
import semsem.chatbot.prompt.PromptTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Registry for managing and caching loaded prompts.
 * Provides easy access to prompts by name with automatic loading from configured paths.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromptRegistry {

    private final PromptLoaderFactory loaderFactory;

    @Value("${chatbot.prompts.path:classpath:prompts/}")
    private String promptsPath;

    @Value("${chatbot.prompts.auto-load:true}")
    private boolean autoLoad;

    private final Map<String, PromptDefinition> prompts = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (autoLoad) {
            loadAllPrompts();
        }
    }

    /**
     * Load all prompts from the configured prompts directory.
     */
    public void loadAllPrompts() {
        log.info("Loading prompts from: {}", promptsPath);

        try {
            if (promptsPath.startsWith("classpath:")) {
                loadFromClasspath();
            } else {
                loadFromFileSystem(Path.of(promptsPath));
            }
            log.info("Loaded {} prompts", prompts.size());
        } catch (IOException e) {
            log.warn("Failed to load prompts from {}: {}", promptsPath, e.getMessage());
        }
    }

    private void loadFromClasspath() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String pattern = promptsPath + "**/*.*";

        Resource[] resources = resolver.getResources(pattern);
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null && isPromptFile(filename)) {
                try {
                    String extension = getExtension(filename);
                    PromptLoaderStrategy strategy = loaderFactory.getStrategy(extension);
                    PromptDefinition definition = strategy.load(resource.getInputStream());

                    if (definition.getName() == null) {
                        definition.setName(getNameWithoutExtension(filename));
                    }

                    register(definition.getName(), definition);
                    log.debug("Loaded prompt: {}", definition.getName());
                } catch (Exception e) {
                    log.warn("Failed to load prompt {}: {}", filename, e.getMessage());
                }
            }
        }
    }

    private void loadFromFileSystem(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            log.warn("Prompts directory does not exist: {}", directory);
            return;
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> isPromptFile(p.getFileName().toString()))
                    .forEach(this::loadPromptFile);
        }
    }

    private void loadPromptFile(Path filePath) {
        try {
            PromptDefinition definition = loaderFactory.load(filePath);
            register(definition.getName(), definition);
            log.debug("Loaded prompt: {}", definition.getName());
        } catch (Exception e) {
            log.warn("Failed to load prompt {}: {}", filePath, e.getMessage());
        }
    }

    /**
     * Register a prompt definition.
     */
    public void register(String name, PromptDefinition definition) {
        prompts.put(name.toLowerCase(), definition);
    }

    /**
     * Get a prompt definition by name.
     */
    public Optional<PromptDefinition> get(String name) {
        return Optional.ofNullable(prompts.get(name.toLowerCase()));
    }

    /**
     * Get a prompt definition by name, throwing if not found.
     */
    public PromptDefinition getOrThrow(String name) {
        return get(name).orElseThrow(() ->
                new PromptLoadException("Prompt not found: " + name));
    }

    /**
     * Get a PromptTemplate from a registered prompt.
     */
    public PromptTemplate getTemplate(String name) {
        PromptDefinition definition = getOrThrow(name);
        if (!definition.isSimplePrompt()) {
            throw new PromptLoadException("Prompt '" + name + "' is not a simple template");
        }
        return new PromptTemplate(definition.getTemplate());
    }

    /**
     * Get a ChatPromptTemplate from a registered prompt.
     */
    public ChatPromptTemplate getChatTemplate(String name) {
        PromptDefinition definition = getOrThrow(name);
        if (!definition.isChatPrompt()) {
            throw new PromptLoadException("Prompt '" + name + "' is not a chat prompt");
        }
        return ChatPromptTemplate.builder()
                .systemTemplate(definition.getSystemPrompt())
                .userTemplate(definition.getUserPrompt())
                .assistantTemplate(definition.getAssistantPrompt())
                .build();
    }

    /**
     * Check if a prompt is registered.
     */
    public boolean contains(String name) {
        return prompts.containsKey(name.toLowerCase());
    }

    /**
     * Get all registered prompt names.
     */
    public java.util.Set<String> getPromptNames() {
        return prompts.keySet();
    }

    /**
     * Clear all registered prompts.
     */
    public void clear() {
        prompts.clear();
    }

    /**
     * Reload all prompts from the configured path.
     */
    public void reload() {
        clear();
        loadAllPrompts();
    }

    private boolean isPromptFile(String filename) {
        String extension = getExtension(filename);
        return loaderFactory.isSupported(extension);
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1) : "";
    }

    private String getNameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }
}
