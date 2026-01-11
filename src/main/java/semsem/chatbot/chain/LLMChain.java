package semsem.chatbot.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Simple chain that invokes an LLM with a prompt template.
 */
@RequiredArgsConstructor
@Builder
public class LLMChain extends BaseChain {

    private final LLMService llmService;
    private final String promptTemplate;
    private final String outputKey;

    @Override
    public String getName() {
        return "LLMChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement prompt formatting and LLM call
        return ChainResult.success("");
    }
}
