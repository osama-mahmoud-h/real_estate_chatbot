package semsem.chatbot.rag.retriever;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;
import java.util.Map;

/**
 * Combines vector and keyword retrieval using Reciprocal Rank Fusion.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class HybridRetriever implements Retriever {

    private final VectorRetriever vectorRetriever;
    private final BM25Retriever bm25Retriever;
    private double vectorWeight = 0.5;
    private double keywordWeight = 0.5;
    private double scoreThreshold = 0.0;

    @Override
    public List<DocumentChunk> retrieve(String query, int topK) {
        // TODO: Implement hybrid retrieval with RRF
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

    protected List<DocumentChunk> reciprocalRankFusion(
            List<DocumentChunk> vectorResults,
            List<DocumentChunk> keywordResults,
            int topK) {
        // TODO: Implement RRF algorithm
        return List.of();
    }
}
