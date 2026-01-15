package semsem.chatbot.chain;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;

/**
 * Chain for conversational interactions with memory.
 */
@RequiredArgsConstructor
@Builder
public class ConversationChain extends BaseChain {

    private final LLMService llmService;
    private final String systemPrompt;

    @Override
    public String getName() {
        return "ConversationChain";
    }

    @Override
    protected ChainResult doInvoke(Map<String, Object> inputs) {
        // TODO: Implement conversation with memory
        return ChainResult.success("");
    }
}
