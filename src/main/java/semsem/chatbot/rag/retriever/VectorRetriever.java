package semsem.chatbot.rag.retriever;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.DocumentChunk;
import semsem.chatbot.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

/**
 * Retrieves documents using vector similarity search.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class VectorRetriever implements Retriever {

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private double scoreThreshold = 0.7;

    @Override
    public List<DocumentChunk> retrieve(String query, int topK) {
        // TODO: Implement vector similarity search
        return List.of();
    }

    @Override
    public List<DocumentChunk> retrieve(String query, int topK, Map<String, Object> filters) {
        // TODO: Implement with metadata filters
        return List.of();
    }

    @Override
    public void setScoreThreshold(double threshold) {
        this.scoreThreshold = threshold;
    }
}
