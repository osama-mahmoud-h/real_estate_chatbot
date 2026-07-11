package semsem.chatbot.rag.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;

import java.util.List;

/**
 * Loads plain text documents.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TextLoader implements DocumentLoader {

    @Override
    public List<Document> load() {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<Document> load(String source) {
        // TODO: Implement text file loading
        return List.of();
    }

    @Override
    public boolean supports(String sourceType) {
        return "txt".equalsIgnoreCase(sourceType) || "text".equalsIgnoreCase(sourceType);
    }
}
