package semsem.chatbot.vectorstore;

import semsem.chatbot.rag.DocumentChunk;

import java.util.List;
import java.util.Map;

/**
 * Interface for vector database operations.
 */
public interface VectorStore {

    void add(DocumentChunk chunk);

    void addAll(List<DocumentChunk> chunks);

    List<DocumentChunk> similaritySearch(float[] queryEmbedding, int topK);

    List<DocumentChunk> similaritySearch(float[] queryEmbedding, int topK, Map<String, Object> filters);

    List<DocumentChunk> similaritySearchWithScore(float[] queryEmbedding, int topK, double minScore);

    void delete(String id);

    void deleteAll(List<String> ids);

    void deleteByMetadata(Map<String, Object> metadata);

    boolean exists(String id);

    long count();
}
