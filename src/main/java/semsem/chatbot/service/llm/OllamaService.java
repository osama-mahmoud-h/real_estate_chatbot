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
 * LLM service implementation for Ollama (local models).
 */
@Slf4j
@Service("ollama")
@RequiredArgsConstructor
public class OllamaService extends BaseLLMService {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${spring.ai.ollama.chat.model:llama3}")
    private String model;

    // TODO: Inject Spring AI OllamaChatModel
    // private final OllamaChatModel chatModel;

    @Override
    public String generate(String prompt, Map<String, Object> options) {
        // TODO: Implement using Spring AI Ollama
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
        return "ollama";
    }

    @Override
    public String getModelName() {
        return model;
    }
}
