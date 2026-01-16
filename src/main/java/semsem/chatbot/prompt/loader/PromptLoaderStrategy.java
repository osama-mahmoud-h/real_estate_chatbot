package semsem.chatbot.prompt.loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;

/**
 * Strategy interface for loading prompts from different file formats.
 */
public interface PromptLoaderStrategy {

    /**
     * Load a prompt definition from a file path.
     */
    PromptDefinition load(Path filePath) throws IOException;

    /**
     * Load a prompt definition from an input stream.
     */
    PromptDefinition load(InputStream inputStream) throws IOException;

    /**
     * Load a prompt definition from string content.
     */
    PromptDefinition loadFromString(String content);

    /**
     * Get the file extensions supported by this loader.
     */
    Set<String> getSupportedExtensions();

    /**
     * Check if this loader supports the given file extension.
     */
    default boolean supports(String extension) {
        return getSupportedExtensions().contains(extension.toLowerCase());
    }
}
