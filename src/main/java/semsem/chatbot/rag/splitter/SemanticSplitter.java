package semsem.chatbot.rag.splitter;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;

/**
 * Splits text based on semantic similarity.
 * Uses embeddings to find natural break points.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Setter
public class SemanticSplitter implements TextSplitter {

    private final EmbeddingService embeddingService;
    private int chunkSize = 1000;
    private int chunkOverlap = 200;
    private double similarityThreshold = 0.8;

    @Override
    public List<DocumentChunk> split(Document document) {
        // TODO: Implement
        return split(document.getContent());
    }

    @Override
    public List<DocumentChunk> split(String text) {
        // TODO: Implement semantic-based splitting
        return List.of();
    }

    @Override
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public void setChunkOverlap(int chunkOverlap) {
        this.chunkOverlap = chunkOverlap;
    }
}
