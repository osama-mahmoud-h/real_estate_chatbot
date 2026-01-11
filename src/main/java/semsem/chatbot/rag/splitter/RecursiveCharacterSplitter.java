package semsem.chatbot.rag.splitter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.rag.Document;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;

/**
 * Splits text recursively by different separators.
 * Tries to keep semantic units together.
 */
@Slf4j
@Component
@Setter
public class RecursiveCharacterSplitter implements TextSplitter {

    private int chunkSize = 1000;
    private int chunkOverlap = 200;
    private List<String> separators = List.of("\n\n", "\n", ". ", " ", "");

    @Override
    public List<DocumentChunk> split(Document document) {
        // TODO: Implement
        return split(document.getContent());
    }

    @Override
    public List<DocumentChunk> split(String text) {
        // TODO: Implement recursive splitting
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
