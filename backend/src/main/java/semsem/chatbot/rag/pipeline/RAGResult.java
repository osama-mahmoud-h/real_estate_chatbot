package semsem.chatbot.rag.pipeline;

import lombok.Builder;
import lombok.Data;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;

/**
 * Result from RAG query including answer and sources.
 */
@Data
@Builder
public class RAGResult {

    private String answer;
    private List<DocumentChunk> sourceDocuments;
    private String query;
    private int tokensUsed;
    private long latencyMs;
}
