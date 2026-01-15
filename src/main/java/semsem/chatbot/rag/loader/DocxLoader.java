package semsem.chatbot.rag.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;

import java.util.List;

/**
 * Loads documents from DOCX files.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocxLoader implements DocumentLoader {

    @Override
    public List<Document> load() {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<Document> load(String source) {
        // TODO: Implement DOCX parsing using Apache POI
        return List.of();
    }

    @Override
    public boolean supports(String sourceType) {
        return "docx".equalsIgnoreCase(sourceType) || "doc".equalsIgnoreCase(sourceType);
    }
}
