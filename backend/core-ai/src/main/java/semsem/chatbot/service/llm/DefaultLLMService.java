package semsem.chatbot.service.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import semsem.chatbot.service.llm.dto.LLMRequest;
import semsem.chatbot.service.llm.dto.LLMResponse;
import semsem.chatbot.service.llm.strategy.ChatLLMFactory;
import semsem.chatbot.service.llm.strategy.ChatLLMStrategy;

import java.util.List;
import java.util.Map;

/**
 * Default LLM service implementation that delegates to the configured ChatLLMStrategy.
 */
@Service
@RequiredArgsConstructor
public class DefaultLLMService implements LLMService {

    private final ChatLLMFactory chatLLMFactory;

    private ChatLLMStrategy getStrategy() {
        return chatLLMFactory.getAvailableStrategy();
    }

    @Override
    public String generate(String prompt) {
        return getStrategy().generate(prompt);
    }

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        return getStrategy().generate(prompt, options);
    }

    @Override
    public LLMResponse generateWithMetadata(LLMRequest request) {
        return getStrategy().generateWithMetadata(request);
    }

    @Override
    public Flux<String> generateStream(String prompt) {
        return getStrategy().generateStream(prompt);
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        return getStrategy().generateStream(prompt, options);
    }

    @Override
    public String chat(List<Map<String, String>> messages) {
        return getStrategy().chat(messages);
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        return getStrategy().chat(messages, options);
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        return getStrategy().chatStream(messages);
    }

    @Override
    public String getProviderName() {
        return getStrategy().getProvider().getKey();
    }

    @Override
    public String getModelName() {
        return getStrategy().getModelName();
    }
}
