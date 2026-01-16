package semsem.chatbot.service.embedding.strategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for selecting Embedding strategies.
 * Supports both cloud (Gemini, Cohere) and local (Ollama) providers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingFactory {

    private final List<EmbeddingStrategy> strategies;
    private final Map<EmbeddingProvider, EmbeddingStrategy> strategyMap = new EnumMap<>(EmbeddingProvider.class);

    @Value("${embedding.default-provider:cohere}")
    private String defaultProviderKey;

    @PostConstruct
    public void init() {
        strategies.forEach(strategy -> {
            strategyMap.put(strategy.getProvider(), strategy);
            log.info("Registered Embedding strategy: {} (available: {}, dimensions: {})",
                    strategy.getProvider(), strategy.isAvailable(), strategy.getDimensions());
        });
    }

    /**
     * Get strategy by provider enum.
     */
    public EmbeddingStrategy getStrategy(EmbeddingProvider provider) {
        return Optional.ofNullable(strategyMap.get(provider))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No Embedding strategy found for provider: " + provider));
    }

    /**
     * Get strategy by provider key string.
     */
    public EmbeddingStrategy getStrategy(String providerKey) {
        return getStrategy(EmbeddingProvider.fromKey(providerKey));
    }

    /**
     * Get the default configured strategy.
     */
    public EmbeddingStrategy getDefaultStrategy() {
        return getStrategy(defaultProviderKey);
    }

    /**
     * Get first available cloud strategy.
     */
    public Optional<EmbeddingStrategy> getAvailableCloudStrategy() {
        return strategies.stream()
                .filter(s -> s.getProvider().isCloud())
                .filter(EmbeddingStrategy::isAvailable)
                .findFirst();
    }

    /**
     * Get first available local strategy.
     */
    public Optional<EmbeddingStrategy> getAvailableLocalStrategy() {
        return strategies.stream()
                .filter(s -> s.getProvider().isLocal())
                .filter(EmbeddingStrategy::isAvailable)
                .findFirst();
    }

    /**
     * Get first available strategy (prefers default, then any available).
     */
    public EmbeddingStrategy getAvailableStrategy() {
        EmbeddingStrategy defaultStrategy = getDefaultStrategy();
        if (defaultStrategy.isAvailable()) {
            return defaultStrategy;
        }

        return strategies.stream()
                .filter(EmbeddingStrategy::isAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available Embedding strategy found"));
    }

    /**
     * Check if a specific provider is available.
     */
    public boolean isProviderAvailable(EmbeddingProvider provider) {
        EmbeddingStrategy strategy = strategyMap.get(provider);
        return strategy != null && strategy.isAvailable();
    }

    /**
     * Get all available providers.
     */
    public List<EmbeddingProvider> getAvailableProviders() {
        return strategies.stream()
                .filter(EmbeddingStrategy::isAvailable)
                .map(EmbeddingStrategy::getProvider)
                .toList();
    }

    /**
     * Get embedding dimensions for a specific provider.
     */
    public int getDimensions(EmbeddingProvider provider) {
        return getStrategy(provider).getDimensions();
    }
}
