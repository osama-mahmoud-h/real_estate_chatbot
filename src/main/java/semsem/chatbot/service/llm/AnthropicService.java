package semsem.chatbot.service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import semsem.chatbot.service.llm.dto.LLMRequest;

import java.util.List;
import java.util.Map;

/**
 * LLM service implementation for Anthropic Claude.
 */
@Slf4j
@Service("anthropic")
@RequiredArgsConstructor
public class AnthropicService extends BaseLLMService {

    @Value("${spring.ai.anthropic.api-key:}")
    private String apiKey;

    @Value("${spring.ai.anthropic.chat.model:claude-3-sonnet}")
    private String model;

    // TODO: Inject Spring AI AnthropicChatModel

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        // TODO: Implement using Spring AI Anthropic
        return "";
    }

    @Override
    public Flux<String> generateStream(String prompt, Map<String, Object> options) {
        // TODO: Implement streaming
        return Flux.empty();
    }

    @Override
    public String chat(List<Map<String, String>> messages, Map<String, Object> options) {
        // TODO: Implement chat
        return "";
    }

    @Override
    public Flux<String> chatStream(List<Map<String, String>> messages) {
        // TODO: Implement streaming chat
        return Flux.empty();
    }

    @Override
    protected String doGenerate(LLMRequest request) {
        // TODO: Implement
        return "";
    }

    @Override
    public String getProviderName() {
        return "anthropic";
    }

    @Override
    public String getModelName() {
        return model;
    }
}
