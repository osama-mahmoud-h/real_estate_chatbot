package semsem.chatbot.llm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;
import semsem.chatbot.llm.domain.entity.ChatLlmModel;
import semsem.chatbot.llm.domain.entity.EmbeddingLlmModel;
import semsem.chatbot.llm.domain.entity.FlowStepLlmConfig;
import semsem.chatbot.llm.domain.entity.provider.LlmProvider;
import semsem.chatbot.llm.repository.FlowStepLlmConfigRepository;
import semsem.chatbot.llm.repository.LlmModelRepository;
import semsem.chatbot.llm.repository.LlmProviderRepository;

import java.util.Optional;

/**
 * Resolves the effective {@link ResolvedChatModel} or {@link EmbeddingLlmModel} from the database.
 *
 * <p>Resolution order for workflow steps:
 * <ol>
 *   <li>Active {@link FlowStepLlmConfig} matching {@code (flowName, stepName)} in {@code llm_flow_step_configs}</li>
 *   <li>Fallback to global default key from {@code .env} ({@code llm.default-chat-key}, e.g. {@code openrouter_gemma31b})</li>
 * </ol>
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class LlmModelResolver {

    private final LlmProviderRepository providerRepository;
    private final LlmModelRepository modelRepository;
    private final FlowStepLlmConfigRepository flowStepConfigRepository;
    private final String defaultChatKey;
    private final String defaultEmbeddingKey;

    @Autowired
    public LlmModelResolver(
            LlmProviderRepository providerRepository,
            LlmModelRepository modelRepository,
            FlowStepLlmConfigRepository flowStepConfigRepository,
            @Value("${llm.default-chat-key:openrouter_gemma31b}") String defaultChatKey,
            @Value("${llm.default-embedding-key:cohere_embed_multilingual_v3}") String defaultEmbeddingKey) {
        this.providerRepository = providerRepository;
        this.modelRepository = modelRepository;
        this.flowStepConfigRepository = flowStepConfigRepository;
        this.defaultChatKey = defaultChatKey;
        this.defaultEmbeddingKey = defaultEmbeddingKey;
    }

    public ResolvedChatModel resolveForStep(String flowName, String stepName) {
        if (flowName != null && !flowName.isBlank() && stepName != null && !stepName.isBlank()) {
            String normalizedFlow = flowName.strip();
            String normalizedStep = stepName.strip();
            log.debug("Resolving ChatLlmModel for workflow step [flow={}, step={}]", normalizedFlow, normalizedStep);

            try {
                Optional<FlowStepLlmConfig> stepConfigOpt =
                        flowStepConfigRepository.findActiveStepConfig(normalizedFlow, normalizedStep);
                if (stepConfigOpt.isPresent()) {
                    FlowStepLlmConfig stepConfig = stepConfigOpt.get();
                    ResolvedChatModel resolved = ResolvedChatModel.fromStepConfig(stepConfig);
                    log.debug("Resolved step-specific LLM [flow={}, step={}] -> key={} (provider={}, model={}, maxTokens={}, temp={})",
                            normalizedFlow, normalizedStep, resolved.modelKey(), resolved.providerKey(),
                            resolved.modelName(), resolved.maxTokens(), resolved.temperature());
                    return resolved;
                }
                log.debug("No active FlowStepLlmConfig override for [flow={}, step={}]; falling back to default model key '{}'",
                        normalizedFlow, normalizedStep, defaultChatKey);
            } catch (Exception ex) {
                log.error("Failed to resolve step-specific LLM config for [flow={}, step={}]: {}. Falling back to default model key '{}'",
                        normalizedFlow, normalizedStep, ex.getMessage(), defaultChatKey, ex);
            }
        } else {
            log.debug("No flowName/stepName supplied; resolving default chat model '{}'", defaultChatKey);
        }
        return resolveDefaultChatModel();
    }

    public ResolvedChatModel resolveDefaultChatModel() {
        log.debug("Resolving default ChatLlmModel using key='{}'", defaultChatKey);
        return resolveChatModelByKey(defaultChatKey);
    }

    public ResolvedChatModel resolveChatModelByKey(String modelKey) {
        if (modelKey == null || modelKey.isBlank()) {
            log.error("Cannot resolve ChatLlmModel: modelKey is null or blank");
            throw new IllegalArgumentException("Chat LLM modelKey must not be blank");
        }
        try {
            ChatLlmModel model = modelRepository.findEnabledChatModelByKey(modelKey.strip())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Chat LLM model not found or disabled in database for key: " + modelKey));
            ResolvedChatModel resolved = ResolvedChatModel.fromDefault(model);
            log.debug("Resolved ChatLlmModel by key='{}' -> provider='{}', modelName='{}', baseUrl='{}'",
                    resolved.modelKey(), resolved.providerKey(), resolved.modelName(), resolved.baseUrl());
            return resolved;
        } catch (IllegalArgumentException ex) {
            log.error("ChatLlmModel lookup failed for key='{}': {}", modelKey, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Database error while resolving ChatLlmModel for key='{}': {}", modelKey, ex.getMessage(), ex);
            throw new IllegalStateException("Failed to resolve ChatLlmModel for key '" + modelKey + "': " + ex.getMessage(), ex);
        }
    }

    public EmbeddingLlmModel resolveDefaultEmbeddingModel() {
        log.debug("Resolving default EmbeddingLlmModel using key='{}'", defaultEmbeddingKey);
        return resolveEmbeddingModelByKey(defaultEmbeddingKey);
    }

    public EmbeddingLlmModel resolveEmbeddingModelByKey(String modelKey) {
        if (modelKey == null || modelKey.isBlank()) {
            log.error("Cannot resolve EmbeddingLlmModel: modelKey is null or blank");
            throw new IllegalArgumentException("Embedding LLM modelKey must not be blank");
        }
        try {
            EmbeddingLlmModel model = modelRepository.findEnabledEmbeddingModelByKey(modelKey.strip())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Embedding LLM model not found or disabled in database for key: " + modelKey));
            log.debug("Resolved EmbeddingLlmModel by key='{}' -> provider='{}', modelName='{}', dimensions={}",
                    model.getModelKey(), model.getProvider().getProviderKey(), model.getModelName(), model.getDimensions());
            return model;
        } catch (IllegalArgumentException ex) {
            log.error("EmbeddingLlmModel lookup failed for key='{}': {}", modelKey, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Database error while resolving EmbeddingLlmModel for key='{}': {}", modelKey, ex.getMessage(), ex);
            throw new IllegalStateException("Failed to resolve EmbeddingLlmModel for key '" + modelKey + "': " + ex.getMessage(), ex);
        }
    }

    public Optional<LlmProvider> findProvider(String providerKey) {
        if (providerKey == null || providerKey.isBlank()) {
            log.warn("findProvider called with null or blank providerKey");
            return Optional.empty();
        }
        try {
            log.debug("Looking up LlmProvider for providerKey='{}'", providerKey);
            return providerRepository.findByProviderKeyIgnoreCase(providerKey.strip());
        } catch (Exception ex) {
            log.error("Error querying LlmProvider for key='{}': {}", providerKey, ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public LlmProvider getProviderOrThrow(String providerKey) {
        return findProvider(providerKey)
                .orElseThrow(() -> {
                    log.error("Required LlmProvider not found in database for key='{}'", providerKey);
                    return new IllegalArgumentException("Provider not found in database: " + providerKey);
                });
    }
}
