package semsem.chatbot.rag.splitter;

import semsem.chatbot.rag.Document;
import semsem.chatbot.rag.DocumentChunk;

import java.util.List;

/**
 * Interface for splitting documents into chunks.
 */
public interface TextSplitter {

    List<DocumentChunk> split(Document document);

    List<DocumentChunk> split(String text);

    void setChunkSize(int chunkSize);

    void setChunkOverlap(int chunkOverlap);
}
