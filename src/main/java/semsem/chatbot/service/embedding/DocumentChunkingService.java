package semsem.chatbot.service.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.rag.Document;
import semsem.chatbot.rag.DocumentChunk;
import semsem.chatbot.rag.splitter.TextSplitter;

import java.util.List;

/**
 * Service for chunking documents and generating embeddings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentChunkingService {

    private final TextSplitter textSplitter;
    private final EmbeddingService embeddingService;

    public List<DocumentChunk> chunkAndEmbed(Document document) {
        // TODO: Implement chunking and embedding
        return List.of();
    }

    public List<DocumentChunk> chunkAndEmbed(List<Document> documents) {
        // TODO: Implement batch processing
        return List.of();
    }

    public List<DocumentChunk> chunk(Document document) {
        return textSplitter.split(document);
    }
}
