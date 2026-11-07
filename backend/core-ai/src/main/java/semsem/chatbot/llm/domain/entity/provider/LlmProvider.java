package semsem.chatbot.llm.domain.entity.provider;

import semsem.chatbot.llm.domain.entity.ChatLlmModel;
import semsem.chatbot.llm.domain.entity.EmbeddingLlmModel;
import semsem.chatbot.llm.domain.entity.LlmModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.*;

/**
 * Aggregate root representing an LLM provider (either {@link CloudLlmProvider} or {@link LocalLlmProvider}).
 * Owns its catalog of {@link LlmModel}s ({@link ChatLlmModel} and {@link EmbeddingLlmModel}).
 */
@Entity
@Table(name = "llm_providers", indexes = {
        @Index(name = "idx_llm_providers_key", columnList = "provider_key", unique = true)
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "provider_type", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter
@ToString(exclude = "models")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LlmProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "provider_key", nullable = false, unique = true, length = 50)
    private String providerKey;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "base_url", nullable = false, length = 255)
    private String baseUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_config", columnDefinition = "jsonb")
    private Map<String, Object> extraConfig = new HashMap<>();

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LlmModel> models = new ArrayList<>();

    protected LlmProvider(String providerKey, String displayName, String baseUrl) {
        if (providerKey == null || providerKey.isBlank()) {
            throw new IllegalArgumentException("Provider key is required");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Provider base URL is required");
        }
        Instant now = Instant.now();
        this.providerKey = providerKey.strip().toLowerCase();
        this.displayName = (displayName == null || displayName.isBlank()) ? this.providerKey : displayName.strip();
        this.baseUrl = baseUrl.strip();
        this.enabled = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public abstract String getApiKey();

    /**
     * Returns the API key required by an SDK client:
     * {@link CloudLlmProvider} validates and returns the resolved secret from {@code .env};
     * {@link LocalLlmProvider} returns a non-empty placeholder since local servers require no secret.
     */
    public abstract String requireApiKey();

    public abstract boolean isCloud();

    public abstract boolean isLocal();

    public abstract boolean isAvailable();

    /**
     * Returns the HTTP read/response timeout in seconds for calls to this provider
     * (defaults to 120 seconds unless overridden in {@code extra_config} or subclass).
     */
    public int getEffectiveTimeoutSeconds() {
        if (extraConfig != null && extraConfig.get("timeout_seconds") instanceof Number num && num.intValue() > 0) {
            return num.intValue();
        }
        return 120;
    }

    /**
     * Combines provider-level options (such as local {@code keep_alive}) with model/step {@code extraConfig}.
     */
    public Map<String, Object> mergeProviderOptions(Map<String, Object> modelExtraConfig) {
        Map<String, Object> merged = new HashMap<>();
        if (extraConfig != null) {
            merged.putAll(extraConfig);
        }
        if (modelExtraConfig != null) {
            merged.putAll(modelExtraConfig);
        }
        return Collections.unmodifiableMap(merged);
    }

    /** Unmodifiable view: mutate through {@link #registerModel(LlmModel)}. */
    public List<LlmModel> getModels() {
        return Collections.unmodifiableList(models);
    }

    public <T extends LlmModel> T registerModel(T model) {
        if (model == null) {
            throw new IllegalArgumentException("Cannot register a null model");
        }
        models.removeIf(existing -> existing.getModelKey().equalsIgnoreCase(model.getModelKey()));
        model.assignTo(this);
        models.add(model);
        touch();
        return model;
    }

    public Optional<LlmModel> findModel(String keyOrName) {
        if (keyOrName == null || models == null) {
            return Optional.empty();
        }
        return models.stream()
                .filter(m -> keyOrName.equalsIgnoreCase(m.getModelKey())
                        || keyOrName.equalsIgnoreCase(m.getModelName()))
                .findFirst();
    }

    public Optional<ChatLlmModel> findChatModel(String keyOrName) {
        return findModel(keyOrName)
                .filter(ChatLlmModel.class::isInstance)
                .map(ChatLlmModel.class::cast);
    }

    public Optional<EmbeddingLlmModel> findEmbeddingModel(String keyOrName) {
        return findModel(keyOrName)
                .filter(EmbeddingLlmModel.class::isInstance)
                .map(EmbeddingLlmModel.class::cast);
    }

    public void changeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Base URL cannot be blank");
        }
        this.baseUrl = baseUrl.strip();
        touch();
    }

    public void configureExtra(Map<String, Object> extraConfig) {
        this.extraConfig = extraConfig != null ? new HashMap<>(extraConfig) : new HashMap<>();
        touch();
    }

    public void enable() {
        this.enabled = true;
        touch();
    }

    public void disable() {
        this.enabled = false;
        touch();
    }

    protected void touch() {
        this.updatedAt = Instant.now();
    }
}
