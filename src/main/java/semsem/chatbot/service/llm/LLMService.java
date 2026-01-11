package semsem.chatbot.service.llm;

import reactor.core.publisher.Flux;
import semsem.chatbot.service.llm.dto.LLMRequest;
import semsem.chatbot.service.llm.dto.LLMResponse;

import java.util.List;
import java.util.Map;

/**
 * Interface for LLM interactions.
 */
public interface LLMService {

    String generate(String prompt);

    String generate(String prompt, Map<String, Object> options);

    LLMResponse generateWithMetadata(LLMRequest request);

    Flux<String> generateStream(String prompt);

    Flux<String> generateStream(String prompt, Map<String, Object> options);

    String chat(List<Map<String, String>> messages);

    String chat(List<Map<String, String>> messages, Map<String, Object> options);

    Flux<String> chatStream(List<Map<String, String>> messages);

    String getProviderName();

    String getModelName();
}
