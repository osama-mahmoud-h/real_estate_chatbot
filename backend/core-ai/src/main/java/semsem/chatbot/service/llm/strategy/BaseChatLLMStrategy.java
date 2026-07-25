package semsem.chatbot.service.llm.strategy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import semsem.chatbot.service.llm.dto.LLMRequest;
import semsem.chatbot.service.llm.dto.LLMResponse;

import java.util.List;
import java.util.Map;

/**
 * Base implementation for ChatLLMStrategy with common functionality.
 */
@Slf4j
@Getter
@Setter
public abstract class BaseChatLLMStrategy implements ChatLLMStrategy {

    protected String modelName;
    protected double defaultTemperature = 0.7;
    protected int defaultMaxTokens = 2048;

    @Override
    public String generate(String prompt) {
        return generate(prompt, Map.of());
    }

    @Override
    public Flux<String> generateStream(String prompt) {
        return generateStream(prompt, Map.of());
    }

    @Override
    public String chat(List<Map<String, String>> messages) {
        return chat(messages, Map.of());
    }

    @Override
    public LLMResponse generateWithMetadata(LLMRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            String content = doGenerate(request);
            return LLMResponse.builder()
                    .content(content)
                    .model(getModelName())
                    .provider(getProvider().getKey())
                    .latencyMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (Exception e) {
            log.error("LLM generation failed for provider {}", getProvider(), e);
            return LLMResponse.builder()
                    .content("")
                    .provider(getProvider().getKey())
                    .latencyMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    /**
     * Internal generation method to be implemented by specific providers.
     */
    protected abstract String doGenerate(LLMRequest request);
}
