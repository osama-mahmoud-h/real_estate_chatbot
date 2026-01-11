package semsem.chatbot.rag.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;

import java.util.List;

/**
 * Loads documents from web URLs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebLoader implements DocumentLoader {

    @Override
    public List<Document> load() {
        // TODO: Implement
        return List.of();
    }

    @Override
    public List<Document> load(String url) {
        // TODO: Implement web scraping using JSoup
        return List.of();
    }

    @Override
    public boolean supports(String sourceType) {
        return "url".equalsIgnoreCase(sourceType) || "web".equalsIgnoreCase(sourceType);
    }
}
