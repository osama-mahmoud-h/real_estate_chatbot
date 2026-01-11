package semsem.chatbot.model.dto.rag;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Request for document ingestion.
 */
@Data
@Builder
public class DocumentIngestRequest {

    private String source;
    private String sourceType; // pdf, docx, url, text
    private String content;
    private Map<String, Object> metadata;
    private Integer chunkSize;
    private Integer chunkOverlap;
}
