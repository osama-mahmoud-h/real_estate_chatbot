package semsem.chatbot.embedding.strategy;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.service.LlmModelResolver;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for selecting Embedding strategies using database-resolved default embedding model.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingFactory {

    private final List<EmbeddingStrategy> strategies;
    private final LlmModelResolver modelResolver;
    private final Map<EmbeddingProvider, EmbeddingStrategy> strategyMap = new EnumMap<>(EmbeddingProvider.class);

    @PostConstruct
    public void init() {
        strategies.forEach(strategy -> {
            strategyMap.put(strategy.getProvider(), strategy);
            log.info("Registered Embedding strategy: {}", strategy.getProvider());
        });
    }

    public EmbeddingStrategy getStrategy(EmbeddingProvider provider) {
        return Optional.ofNullable(strategyMap.get(provider))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No Embedding strategy found for provider: " + provider));
    }

    public EmbeddingStrategy getStrategy(String providerKey) {
        return getStrategy(EmbeddingProvider.fromKey(providerKey));
    }

    public EmbeddingStrategy getDefaultStrategy() {
        String providerKey = modelResolver.resolveDefaultEmbeddingModel().getProvider().getProviderKey();
        return getStrategy(providerKey);
    }

    public Optional<EmbeddingStrategy> getAvailableCloudStrategy() {
        return strategies.stream()
                .filter(s -> s.getProvider().isCloud())
                .filter(EmbeddingStrategy::isAvailable)
                .findFirst();
    }

    public Optional<EmbeddingStrategy> getAvailableLocalStrategy() {
        return strategies.stream()
                .filter(s -> s.getProvider().isLocal())
                .filter(EmbeddingStrategy::isAvailable)
                .findFirst();
    }

    public EmbeddingStrategy getAvailableStrategy() {
        try {
            EmbeddingStrategy defaultStrategy = getDefaultStrategy();
            if (defaultStrategy.isAvailable()) {
                return defaultStrategy;
            }
        } catch (Exception ex) {
            log.debug("Default embedding strategy unavailable, searching fallback: {}", ex.getMessage());
        }

        return strategies.stream()
                .filter(EmbeddingStrategy::isAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available Embedding strategy found"));
    }

    public boolean isProviderAvailable(EmbeddingProvider provider) {
        EmbeddingStrategy strategy = strategyMap.get(provider);
        return strategy != null && strategy.isAvailable();
    }

    public List<EmbeddingProvider> getAvailableProviders() {
        return strategies.stream()
                .filter(EmbeddingStrategy::isAvailable)
                .map(EmbeddingStrategy::getProvider)
                .toList();
    }

    public int getDimensions(EmbeddingProvider provider) {
        return getStrategy(provider).getDimensions();
    }
}
