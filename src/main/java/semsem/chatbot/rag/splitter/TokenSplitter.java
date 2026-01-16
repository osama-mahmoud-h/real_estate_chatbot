package semsem.chatbot.rag.splitter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;

/**
 * Splits text by token count.
 * Useful for respecting LLM context limits.
 */
@Slf4j
@Component
@Setter
public class TokenSplitter implements TextSplitter {

    private int chunkSize = 500; // tokens
    private int chunkOverlap = 50; // tokens
    private String encoding = "cl100k_base";

    @Override
    public List<DocumentChunk> split(Document document) {
        // TODO: Implement
        return split(document.getContent());
    }

    @Override
    public List<DocumentChunk> split(String text) {
        // TODO: Implement token-based splitting
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
