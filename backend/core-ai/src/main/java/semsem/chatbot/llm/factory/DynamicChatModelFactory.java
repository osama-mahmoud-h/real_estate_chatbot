package semsem.chatbot.llm.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;
import semsem.chatbot.llm.factory.strategy.ChatModelBuilderStrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory + Strategy chain that dynamically constructs and caches Spring AI {@link ChatModel}
 * instances from database-backed {@link ResolvedChatModel} configurations.
 */
@Slf4j
@Component
public class DynamicChatModelFactory {

    private final List<ChatModelBuilderStrategy> strategies;
    private final Map<String, ChatModel> modelCache = new ConcurrentHashMap<>();

    public DynamicChatModelFactory(List<ChatModelBuilderStrategy> strategies) {
        this.strategies = strategies.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();
        log.debug("Initialized DynamicChatModelFactory with {} strategies: {}",
                this.strategies.size(),
                this.strategies.stream().map(s -> s.getClass().getSimpleName()).toList());
    }

    public ChatModel getOrCreate(ResolvedChatModel resolved) {
        if (resolved == null) {
            log.error("Cannot create ChatModel: ResolvedChatModel is null");
            throw new IllegalArgumentException("ResolvedChatModel must not be null");
        }

        try {
            String cacheKey = buildCacheKey(resolved);
            ChatModel cached = modelCache.get(cacheKey);
            if (cached != null) {
                log.debug("ChatModel cache HIT for key={} [provider={}, model={}]",
                        resolved.modelKey(), resolved.providerKey(), resolved.modelName());
                return cached;
            }

            log.debug("ChatModel cache MISS for key={} [provider={}, model={}] — constructing instance",
                    resolved.modelKey(), resolved.providerKey(), resolved.modelName());
            return modelCache.computeIfAbsent(cacheKey, key -> createChatModel(resolved));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("Failed to obtain ChatModel for key={} [provider={}, model={}]: {}",
                    resolved.modelKey(), resolved.providerKey(), resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error obtaining ChatModel for key={} [provider={}, model={}]: {}",
                    resolved.modelKey(), resolved.providerKey(), resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to build ChatModel for '" + resolved.modelKey() + "': " + ex.getMessage(), ex);
        }
    }

    public void evictCache() {
        int size = modelCache.size();
        modelCache.clear();
        log.info("Evicted {} cached ChatModel instance(s) from DynamicChatModelFactory", size);
    }

    private ChatModel createChatModel(ResolvedChatModel resolved) {
        log.info("Creating dynamic ChatModel for key={} [provider={}, model={}, baseUrl={}, maxTokens={}, temp={}]",
                resolved.modelKey(), resolved.providerKey(), resolved.modelName(),
                resolved.baseUrl(), resolved.maxTokens(), resolved.temperature());

        ChatModelBuilderStrategy matchedStrategy = null;
        for (ChatModelBuilderStrategy strategy : strategies) {
            try {
                if (strategy.supports(resolved)) {
                    matchedStrategy = strategy;
                    break;
                }
            } catch (Exception ex) {
                log.warn("Strategy {} threw exception while checking support for provider '{}': {}",
                        strategy.getClass().getSimpleName(), resolved.providerKey(), ex.getMessage());
            }
        }

        if (matchedStrategy == null) {
            log.error("No ChatModelBuilderStrategy found supporting provider='{}' (modelKey='{}')",
                    resolved.providerKey(), resolved.modelKey());
            throw new IllegalStateException(
                    "No ChatModelBuilderStrategy found for provider '" + resolved.providerKey() + "'"
            );
        }

        try {
            log.debug("Selected strategy {} for provider='{}', model='{}'",
                    matchedStrategy.getClass().getSimpleName(), resolved.providerKey(), resolved.modelName());
            return matchedStrategy.build(resolved);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            log.error("Strategy {} failed validation for provider='{}', model='{}': {}",
                    matchedStrategy.getClass().getSimpleName(), resolved.providerKey(), resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Strategy {} failed to build ChatModel for provider='{}', model='{}': {}",
                    matchedStrategy.getClass().getSimpleName(), resolved.providerKey(), resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException(
                    "Failed to build ChatModel via " + matchedStrategy.getClass().getSimpleName() + ": " + ex.getMessage(), ex);
        }
    }

    private String buildCacheKey(ResolvedChatModel resolved) {
        return String.join("|",
                resolved.providerKey(),
                resolved.modelKey(),
                resolved.modelName(),
                String.valueOf(resolved.baseUrl()),
                String.valueOf(resolved.maxTokens()),
                String.valueOf(resolved.temperature()),
                String.valueOf(resolved.topP()),
                String.valueOf(resolved.extraConfig()));
    }
}
