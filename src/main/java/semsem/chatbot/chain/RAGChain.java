package semsem.chatbot.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.rag.retriever.Retriever;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Chain that performs retrieval-augmented generation.
 * Retrieves relevant documents, then generates response with context.
 */
@RequiredArgsConstructor
@Builder
public class RAGChain extends BaseChain {

    private final Retriever retriever;
    private final LLMService llmService;
    private final String promptTemplate;
    private final int topK;

    @Override
    public String getName() {
        return "RAGChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement retrieval and generation
        return ChainResult.success("");
    }
}
