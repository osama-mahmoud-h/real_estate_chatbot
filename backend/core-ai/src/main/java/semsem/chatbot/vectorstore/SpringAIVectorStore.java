package semsem.chatbot.vectorstore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;
import java.util.Map;

/**
 * VectorStore implementation using Spring AI's VectorStore abstraction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpringAIVectorStore implements VectorStore {

    // TODO: Inject Spring AI VectorStore
    // private final org.springframework.ai.vectorstore.VectorStore vectorStore;

    @Override
    public void add(DocumentChunk chunk) {
        // TODO: Implement using Spring AI
    }

    @Override
    public void addAll(List<DocumentChunk> chunks) {
        // TODO: Implement using Spring AI
    }

    @Override
    public List<DocumentChunk> similaritySearch(float[] queryEmbedding, int topK) {
        // TODO: Implement using Spring AI
        return List.of();
    }

    @Override
    public List<DocumentChunk> similaritySearch(float[] queryEmbedding, int topK, Map<String, Object> filters) {
        // TODO: Implement using Spring AI with filters
        return List.of();
    }

    @Override
    public List<DocumentChunk> similaritySearchWithScore(float[] queryEmbedding, int topK, double minScore) {
        // TODO: Implement
        return List.of();
    }

    @Override
    public void delete(String id) {
        // TODO: Implement
    }

    @Override
    public void deleteAll(List<String> ids) {
        // TODO: Implement
    }

    @Override
    public void deleteByMetadata(Map<String, Object> metadata) {
        // TODO: Implement
    }

    @Override
    public boolean exists(String id) {
        // TODO: Implement
        return false;
    }

    @Override
    public long count() {
        // TODO: Implement
        return 0;
    }
}
