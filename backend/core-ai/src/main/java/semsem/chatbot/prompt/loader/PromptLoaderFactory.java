package semsem.chatbot.prompt.loader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.exception.PromptLoadException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Factory for selecting the appropriate prompt loader strategy based on file extension.
 */
@Slf4j
@Component
public class PromptLoaderFactory {

    private final List<PromptLoaderStrategy> strategies;

    public PromptLoaderFactory(List<PromptLoaderStrategy> strategies) {
        this.strategies = strategies;
        log.info("Registered {} prompt loader strategies: {}",
                strategies.size(),
                strategies.stream()
                        .flatMap(s -> s.getSupportedExtensions().stream())
                        .collect(Collectors.joining(", ")));
    }

    /**
     * Get the loader strategy for a specific file extension.
     */
    public PromptLoaderStrategy getStrategy(String extension) {
        String ext = extension.toLowerCase().replace(".", "");
        return strategies.stream()
                .filter(s -> s.supports(ext))
                .findFirst()
                .orElseThrow(() -> new PromptLoadException(
                        "No loader found for extension: " + extension +
                        ". Supported: " + getSupportedExtensions()));
    }

    /**
     * Load a prompt from a file path, automatically selecting the right strategy.
     */
    public PromptDefinition load(Path filePath) throws IOException {
        String fileName = filePath.getFileName().toString();
        String extension = getExtension(fileName);
        PromptLoaderStrategy strategy = getStrategy(extension);
        return strategy.load(filePath);
    }

    /**
     * Load a prompt from string content with explicit format.
     */
    public PromptDefinition loadFromString(String content, String format) {
        PromptLoaderStrategy strategy = getStrategy(format);
        return strategy.loadFromString(content);
    }

    /**
     * Get all supported file extensions.
     */
    public Set<String> getSupportedExtensions() {
        return strategies.stream()
                .flatMap(s -> s.getSupportedExtensions().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Check if a file extension is supported.
     */
    public boolean isSupported(String extension) {
        String ext = extension.toLowerCase().replace(".", "");
        return strategies.stream().anyMatch(s -> s.supports(ext));
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            throw new PromptLoadException("File has no extension: " + fileName);
        }
        return fileName.substring(dotIndex + 1);
    }
}
