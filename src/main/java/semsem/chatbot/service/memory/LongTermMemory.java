package semsem.chatbot.service.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.service.embedding.EmbeddingService;
import semsem.chatbot.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

/**
 * Long-term memory using vector store for semantic retrieval.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LongTermMemory extends ConversationMemory {

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;

    @Override
    public String getFormattedHistory(String conversationId) {
        // TODO: Implement semantic retrieval of relevant history
        return "";
    }

    public List<String> retrieveRelevant(String query, int topK) {
        // TODO: Implement semantic search over conversation history
        return List.of();
    }

    @Override
    public void save(String conversationId) {
        // TODO: Store messages in vector store
    }

    @Override
    public void load(String conversationId) {
        // TODO: Load from vector store
    }
}
