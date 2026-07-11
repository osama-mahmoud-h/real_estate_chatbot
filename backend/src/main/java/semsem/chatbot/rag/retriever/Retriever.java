package semsem.chatbot.rag.retriever;

import semsem.chatbot.rag.DocumentChunk;

import java.util.List;
import java.util.Map;

/**
 * Interface for document retrieval.
 */
public interface Retriever {

    List<DocumentChunk> retrieve(String query, int topK);

    List<DocumentChunk> retrieve(String query, int topK, Map<String, Object> filters);

    void setScoreThreshold(double threshold);
}
