package semsem.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import semsem.chatbot.config.llm.chat.ChatModelConfig;
import semsem.chatbot.config.llm.chat.ChatSelection;
import semsem.chatbot.config.llm.embedding.EmbeddingModelConfig;
import semsem.chatbot.config.llm.embedding.EmbeddingSelection;
import semsem.chatbot.config.llm.chat.IChatModelConfig;
import semsem.chatbot.config.llm.embedding.IEmbeddingModelConfig;
import semsem.chatbot.config.llm.IProviderConfig;
import semsem.chatbot.config.llm.ProviderConfig;
import semsem.chatbot.model.enums.LLMProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Main AI configuration.
 * Clean, LLM-independent architecture with model capabilities.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {

    /** Chat model selection */
    private ChatSelection chat = new ChatSelection();

    /** Embedding model selection */
    private EmbeddingSelection embedding = new EmbeddingSelection();

    /** Provider configurations with models */
    private Map<LLMProvider, ProviderConfig> providers = new HashMap<>();

    // =========================================================================
    // PROVIDER ACCESS
    // =========================================================================

    public Optional<ProviderConfig> getProvider(LLMProvider name) {
        return Optional.ofNullable(providers.get(name));
    }

    public ProviderConfig getProviderOrThrow(LLMProvider name) {
        return getProvider(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Provider not found: " + name));
    }

    // =========================================================================
    // SELECTED PROVIDER ACCESS (using interfaces for DIP compliance)
    // =========================================================================

    /**
     * Get the chat provider configuration.
     */
    public IProviderConfig getChatProvider() {
        return getProviderOrThrow(chat.getProvider());
    }

    /**
     * Get the embedding provider configuration.
     */
    public IProviderConfig getEmbeddingProvider() {
        return getProviderOrThrow(embedding.getProvider());
    }

    // =========================================================================
    // SELECTED MODEL ACCESS (returning proper concrete types)
    // =========================================================================

    /**
     * Get the selected chat model configuration.
     */
    public Optional<IChatModelConfig> getChatModel() {
        return getProviderOrThrow(chat.getProvider())
                .getChatModel(chat.getModel())
                .map(m -> m);
    }

    /**
     * Get the selected embedding model configuration.
     */
    public Optional<IEmbeddingModelConfig> getEmbeddingModel() {
        return getProviderOrThrow(embedding.getProvider())
                .getEmbeddingModel(embedding.getModel())
                .map(m -> m);
    }

    /**
     * Get the selected chat model configuration or throw.
     */
    public IChatModelConfig getChatModelOrThrow() {
        return getChatModel()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat model not found: " + chat.getModel()));
    }

    /**
     * Get the selected embedding model configuration or throw.
     */
    public IEmbeddingModelConfig getEmbeddingModelOrThrow() {
        return getEmbeddingModel()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Embedding model not found: " + embedding.getModel()));
    }

    // =========================================================================
    // CONCRETE TYPE ACCESS (for cases needing full model config)
    // =========================================================================

    /**
     * Get the chat provider as concrete type.
     */
    public ProviderConfig getChatProviderConfig() {
        return getProviderOrThrow(chat.getProvider());
    }

    /**
     * Get the embedding provider as concrete type.
     */
    public ProviderConfig getEmbeddingProviderConfig() {
        return getProviderOrThrow(embedding.getProvider());
    }

    /**
     * Get the chat model as concrete type.
     */
    public Optional<ChatModelConfig> getChatModelConfig() {
        return getChatProviderConfig().getChatModel(chat.getModel());
    }

    /**
     * Get the embedding model as concrete type.
     */
    public Optional<EmbeddingModelConfig> getEmbeddingModelConfig() {
        return getEmbeddingProviderConfig().getEmbeddingModel(embedding.getModel());
    }

    // =========================================================================
    // AVAILABILITY CHECKS
    // =========================================================================

    public boolean isChatProviderAvailable() {
        return getProvider(chat.getProvider())
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }

    public boolean isEmbeddingProviderAvailable() {
        return getProvider(embedding.getProvider())
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }
}