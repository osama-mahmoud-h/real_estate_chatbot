package semsem.chatbot.service.llm.strategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.config.LLMProperties;
import semsem.chatbot.model.enums.LLMProvider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for selecting Chat LLM strategies.
 * Single Responsibility: selects strategy based on AIProperties.chat.provider
 * No if-else: uses Map lookup with LLMProvider enum.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatLLMFactory {

    private final List<ChatLLMStrategy> strategies;
    private final LLMProperties llmProperties;
    private final Map<LLMProvider, ChatLLMStrategy> strategyMap = new EnumMap<>(LLMProvider.class);

    @PostConstruct
    public void init() {
        strategies.forEach(strategy -> {
            strategyMap.put(strategy.getProvider(), strategy);
            log.info("Registered Chat LLM strategy: {} (available: {})",
                    strategy.getProvider(), strategy.isAvailable());
        });
    }

    /**
     * Get strategy by provider enum.
     */
    public ChatLLMStrategy getStrategy(LLMProvider provider) {
        return Optional.ofNullable(strategyMap.get(provider))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No Chat LLM strategy found for provider: " + provider));
    }

    /**
     * Get strategy by provider key string.
     */
    public ChatLLMStrategy getStrategy(String providerKey) {
        return getStrategy(LLMProvider.fromKey(providerKey));
    }

    /**
     * Get the default configured strategy based on AIProperties.chat.provider.
     */
    public ChatLLMStrategy getDefaultStrategy() {
        LLMProvider configuredProvider = llmProperties.getChat().getProvider();
        return getStrategy(configuredProvider);
    }

    /**
     * Get first available cloud strategy.
     */
    public Optional<ChatLLMStrategy> getAvailableCloudStrategy() {
        return strategies.stream()
                .filter(s -> s.getProvider().isCloud())
                .filter(ChatLLMStrategy::isAvailable)
                .findFirst();
    }

    /**
     * Get first available local strategy.
     */
    public Optional<ChatLLMStrategy> getAvailableLocalStrategy() {
        return strategies.stream()
                .filter(s -> s.getProvider().isLocal())
                .filter(ChatLLMStrategy::isAvailable)
                .findFirst();
    }

    /**
     * Get first available strategy (prefers default, then any available).
     */
    public ChatLLMStrategy getAvailableStrategy() {
        ChatLLMStrategy defaultStrategy = getDefaultStrategy();
        if (defaultStrategy.isAvailable()) {
            return defaultStrategy;
        }

        return strategies.stream()
                .filter(ChatLLMStrategy::isAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available Chat LLM strategy found"));
    }

    /**
     * Check if a specific provider is available.
     */
    public boolean isProviderAvailable(LLMProvider provider) {
        ChatLLMStrategy strategy = strategyMap.get(provider);
        return strategy != null && strategy.isAvailable();
    }

    /**
     * Get all available providers.
     */
    public List<LLMProvider> getAvailableProviders() {
        return strategies.stream()
                .filter(ChatLLMStrategy::isAvailable)
                .map(ChatLLMStrategy::getProvider)
                .toList();
    }
}
