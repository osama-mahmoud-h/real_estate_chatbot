package semsem.chatbot.rag.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;

import java.util.List;

/**
 * Loads all documents from a directory.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryLoader implements DocumentLoader {

    private final List<DocumentLoader> loaders;

    @Override
    public List<Document> load() {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<Document> load(String directoryPath) {
        // TODO: Implement recursive directory loading
        return List.of();
    }

    @Override
    public boolean supports(String sourceType) {
        return "directory".equalsIgnoreCase(sourceType) || "dir".equalsIgnoreCase(sourceType);
    }
}
