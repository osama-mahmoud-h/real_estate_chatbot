package semsem.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import semsem.chatbot.config.ai.ChatSelection;
import semsem.chatbot.config.ai.EmbeddingSelection;
import semsem.chatbot.config.ai.ModelConfig;
import semsem.chatbot.config.ai.ProviderConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Main AI configuration.
 * Clean, LLM-independent architecture with model capabilities.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIProperties {

    /** Chat model selection */
    private ChatSelection chat = new ChatSelection();

    /** Embedding model selection */
    private EmbeddingSelection embedding = new EmbeddingSelection();

    /** Provider configurations with models */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    // =========================================================================
    // PROVIDER ACCESS
    // =========================================================================

    public Optional<ProviderConfig> getProvider(String name) {
        return Optional.ofNullable(providers.get(name));
    }

    public ProviderConfig getProviderOrThrow(String name) {
        return getProvider(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Provider not found: " + name));
    }

    // =========================================================================
    // SELECTED PROVIDER/MODEL ACCESS
    // =========================================================================

    public ProviderConfig getChatProvider() {
        return getProviderOrThrow(chat.getProvider());
    }

    public ProviderConfig getEmbeddingProvider() {
        return getProviderOrThrow(embedding.getProvider());
    }

    public Optional<ModelConfig> getChatModel() {
        return getChatProvider().getModel(chat.getModel());
    }

    public Optional<ModelConfig> getEmbeddingModel() {
        return getEmbeddingProvider().getModel(embedding.getModel());
    }

    public ModelConfig getChatModelOrThrow() {
        return getChatModel()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat model not found: " + chat.getModel()));
    }

    public ModelConfig getEmbeddingModelOrThrow() {
        return getEmbeddingModel()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Embedding model not found: " + embedding.getModel()));
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