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
 * LLM service implementation for Google GenAI (Gemini).
 */
@Slf4j
@Service("googlegenai")
@RequiredArgsConstructor
public class GoogleGenAIService extends BaseLLMService {

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.google.genai.chat.model:gemini-pro}")
    private String model;

    // TODO: Inject Spring AI GoogleGenAI ChatModel

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        // TODO: Implement using Spring AI Google GenAI
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
        return "googlegenai";
    }

    @Override
    public String getModelName() {
        return model;
    }
}
