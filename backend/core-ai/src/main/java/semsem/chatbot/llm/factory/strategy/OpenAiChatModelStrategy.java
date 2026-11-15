package semsem.chatbot.llm.factory.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;

import java.time.Duration;
import java.util.Map;

/**
 * Unified strategy that builds an {@link OpenAiChatModel} for any OpenAI-compatible provider —
 * whether Cloud ({@code CloudLlmProvider}: OpenRouter, Cohere, Groq, DeepSeek) or Local
 * ({@code LocalLlmProvider}: Ollama, vLLM, LM Studio, llama.cpp).
 *
 * <p>Cloud-vs-Local credential and option differences are delegated polymorphically to
 * {@link ResolvedChatModel#requireApiKey()} and {@link ResolvedChatModel#effectiveExtraBody()}.
 */
@Slf4j
@Component
@Order(100)
public class OpenAiChatModelStrategy implements ChatModelBuilderStrategy {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);

    @Override
    public boolean supports(ResolvedChatModel resolved) {
        return resolved != null && resolved.provider() != null;
    }

    @Override
    public ChatModel build(ResolvedChatModel resolved) {
        try {
            String normalizedUrl = normalizeBaseUrl(resolved.baseUrl());
            String apiKey = resolved.requireApiKey();
            int timeoutSec = resolved.timeoutSeconds();

            log.debug("Building OpenAiChatModel for provider='{}', model='{}', normalizedBaseUrl='{}', maxTokens={}, temp={}, topP={}, timeout={}s",
                    resolved.providerKey(), resolved.modelName(), normalizedUrl,
                    resolved.maxTokens(), resolved.temperature(), resolved.topP(), timeoutSec);

            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(normalizedUrl)
                    .apiKey(apiKey)
                    .restClientBuilder(createRestClientBuilder(timeoutSec))
                    .webClientBuilder(createWebClientBuilder(timeoutSec))
                    .build();

            OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
                    .model(resolved.modelName())
                    .temperature(resolved.temperature())
                    .maxTokens(resolved.maxTokens())
                    .topP(resolved.topP());

            try {
                Map<String, Object> extraBody = resolved.effectiveExtraBody();
                if (extraBody != null && !extraBody.isEmpty()) {
                    optionsBuilder.extraBody(extraBody);
                    log.debug("Applied extraBody keys {} to OpenAiChatOptions for model='{}'",
                            extraBody.keySet(), resolved.modelName());
                }
            } catch (Exception extraEx) {
                log.warn("Failed to apply extraBody options for model '{}': {}. Proceeding with standard options.",
                        resolved.modelName(), extraEx.getMessage());
            }

            return OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(optionsBuilder.build())
                    .build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("OpenAiChatModel configuration error for provider='{}', model='{}': {}",
                    resolved.providerKey(), resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected failure building OpenAiChatModel for provider='{}', model='{}': {}",
                    resolved.providerKey(), resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to build OpenAiChatModel: " + ex.getMessage(), ex);
        }
    }

    /**
     * Builds a {@link RestClient.Builder} backed by {@link SimpleClientHttpRequestFactory}
     * (which buffers the JSON request body with an explicit {@code Content-Length} header
     * instead of Reactor Netty's streaming chunked transfer that hangs on certain cloud gateways
     * like Cohere's compatibility endpoint) and configures explicit connect/read timeouts.
     */
    static RestClient.Builder createRestClientBuilder(int timeoutSeconds) {
        Duration readTimeout = Duration.ofSeconds(Math.max(timeoutSeconds, 30));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(readTimeout);
        return RestClient.builder().requestFactory(requestFactory);
    }

    static WebClient.Builder createWebClientBuilder(int timeoutSeconds) {
        Duration readTimeout = Duration.ofSeconds(Math.max(timeoutSeconds, 30));
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(readTimeout);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    /**
     * Spring AI's {@link OpenAiApi} appends {@code /v1/chat/completions} by default.
     * Stripping a trailing {@code /v1} or {@code /} from the configured base URL prevents
     * duplicate {@code /v1/v1/chat/completions} paths.
     */
    static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Provider base URL is required");
        }
        String trimmed = baseUrl.strip();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.endsWith("/v1")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed;
    }
}
