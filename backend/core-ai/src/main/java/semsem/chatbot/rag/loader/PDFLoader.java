package semsem.chatbot.rag.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;

import java.util.List;

/**
 * Loads documents from PDF files.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PDFLoader implements DocumentLoader {

    @Override
    public List<Document> load() {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<Document> load(String source) {
        // TODO: Implement PDF parsing using Apache PDFBox
        return List.of();
    }

    @Override
    public boolean supports(String sourceType) {
        return "pdf".equalsIgnoreCase(sourceType);
    }
}
