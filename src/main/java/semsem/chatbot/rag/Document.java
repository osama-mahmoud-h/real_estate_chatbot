package semsem.chatbot.rag;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Represents a document for RAG processing.
 */
@Data
@Builder
public class Document {

    private String id;
    private String content;
    private String source;
    private String sourceType;
    private Map<String, Object> metadata;
}
