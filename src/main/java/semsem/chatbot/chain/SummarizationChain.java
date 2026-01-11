package semsem.chatbot.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Chain for text summarization.
 * Supports map-reduce and refine strategies for long documents.
 */
@RequiredArgsConstructor
@Builder
public class SummarizationChain extends BaseChain {

    private final LLMService llmService;
    private final String strategy; // "map_reduce", "refine", "stuff"
    private final int chunkSize;

    @Override
    public String getName() {
        return "SummarizationChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement summarization
        return ChainResult.success("");
    }
}
