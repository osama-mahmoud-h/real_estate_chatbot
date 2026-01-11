package semsem.chatbot.rag.retriever;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;
import java.util.Map;

/**
 * Retrieves documents using BM25 keyword matching.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class BM25Retriever implements Retriever {

    private double scoreThreshold = 0.0;
    private double k1 = 1.5;
    private double b = 0.75;

    @Override
    public List<DocumentChunk> retrieve(String query, int topK) {
        // TODO: Implement BM25 search
        return List.of();
    }

    @Override
    public List<DocumentChunk> retrieve(String query, int topK, Map<String, Object> filters) {
        // TODO: Implement with filters
        return List.of();
    }

    @Override
    public void setScoreThreshold(double threshold) {
        this.scoreThreshold = threshold;
    }
}
