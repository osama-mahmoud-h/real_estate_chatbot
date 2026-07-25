package semsem.chatbot.rag;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Represents a chunk of a document after splitting.
 */
@Data
@Builder
public class DocumentChunk {

    private String id;
    private String documentId;
    private String content;
    private int chunkIndex;
    private int startOffset;
    private int endOffset;
    private float[] embedding;
    private Map<String, Object> metadata;
}
