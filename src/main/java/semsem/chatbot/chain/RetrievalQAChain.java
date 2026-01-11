package semsem.chatbot.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.rag.retriever.Retriever;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Chain for question-answering over documents.
 */
@RequiredArgsConstructor
@Builder
public class RetrievalQAChain extends BaseChain {

    private final Retriever retriever;
    private final LLMService llmService;
    private final String qaPromptTemplate;
    private final boolean returnSourceDocuments;

    @Override
    public String getName() {
        return "RetrievalQAChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement Q&A
        return ChainResult.success("");
    }
}
