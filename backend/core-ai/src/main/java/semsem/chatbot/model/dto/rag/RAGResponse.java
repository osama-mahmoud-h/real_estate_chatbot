package semsem.chatbot.model.dto.rag;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response from RAG query.
 */
@Data
@Builder
public class RAGResponse {

    private String answer;
    private List<SourceDocument> sources;
    private String query;
    private int tokensUsed;
    private long latencyMs;

    @Data
    @Builder
    public static class SourceDocument {
        private String documentId;
        private String content;
        private double score;
        private String source;
    }
}
