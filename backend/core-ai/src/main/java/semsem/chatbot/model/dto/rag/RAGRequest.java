package semsem.chatbot.model.dto.rag;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request for RAG query.
 */
@Data
@Builder
public class RAGRequest {

    private String query;
    private String conversationId;
    private Integer topK;
    private Double scoreThreshold;
    private Map<String, Object> filters;
    private Boolean returnSources;
}
