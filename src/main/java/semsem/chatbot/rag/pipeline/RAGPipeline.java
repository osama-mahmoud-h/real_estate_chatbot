package semsem.chatbot.rag.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.rag.Document;
import semsem.chatbot.rag.DocumentChunk;
import semsem.chatbot.rag.loader.DocumentLoader;
import semsem.chatbot.rag.retriever.Retriever;
import semsem.chatbot.rag.splitter.TextSplitter;
import semsem.chatbot.service.embedding.EmbeddingService;
import semsem.chatbot.service.llm.LLMService;
import semsem.chatbot.vectorstore.VectorStore;

import java.util.List;

/**
 * Orchestrates the complete RAG pipeline.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RAGPipeline {

    private final List<DocumentLoader> loaders;
    private final TextSplitter textSplitter;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    private final Retriever retriever;
    private final LLMService llmService;

    /**
     * Ingest documents into the vector store.
     */
    public void ingest(List<Document> documents) {
        // TODO: Implement ingestion pipeline
    }

    /**
     * Ingest from a source path.
     */
    public void ingest(String source, String sourceType) {
        // TODO: Load and ingest documents
    }

    /**
     * Query with RAG.
     */
    public String query(String question, int topK) {
        // TODO: Implement query pipeline
        return "";
    }

    /**
     * Query with RAG returning sources.
     */
    public RAGResult queryWithSources(String question, int topK) {
        // TODO: Implement query with sources
        return null;
    }
}
