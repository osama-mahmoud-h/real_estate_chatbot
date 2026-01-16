package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Output from RAG_RETRIEVER node.
 */
@Data
@Builder
public class RagRetrieverOutput {

    private List<RetrievedDocument> documents;
    private String augmentedPrompt;
    private int totalDocumentsFound;

    @Data
    @Builder
    public static class RetrievedDocument {
        private String content;
        private String source;
        private double relevanceScore;
        private Map<String, Object> metadata;
    }
}
