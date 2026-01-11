package semsem.chatbot.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Chain that routes to different sub-chains based on input classification.
 */
@RequiredArgsConstructor
@Builder
public class RouterChain extends BaseChain {

    private final LLMService llmService;
    private final Map<String, Chain> destinationChains;
    private final String routerTemplate;
    private final Chain defaultChain;

    @Override
    public String getName() {
        return "RouterChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement routing logic
        return ChainResult.success("");
    }

    protected String classifyInput(String input) {
        // TODO: Use LLM to classify input
        return null;
    }
}
