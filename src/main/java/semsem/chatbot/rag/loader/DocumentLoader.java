package semsem.chatbot.rag.loader;

import semsem.chatbot.rag.Document;

import java.util.List;

/**
 * Interface for loading documents from various sources.
 */
public interface DocumentLoader {

    List<Document> load();

    List<Document> load(String source);

    boolean supports(String sourceType);
}
