package semsem.chatbot.llm.domain.entity;

import semsem.chatbot.llm.domain.entity.provider.LlmProvider;
import semsem.chatbot.llm.domain.enums.ModelCapability;
import semsem.chatbot.llm.domain.enums.ModelType;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.*;

/**
 * Abstract entity representing a model belonging to an {@link LlmProvider}.
 * Subclasses ({@link ChatLlmModel}, {@link EmbeddingLlmModel}) specialize it via SINGLE_TABLE inheritance.
 */
@Entity
@Table(name = "llm_models", indexes = {
        @Index(name = "idx_llm_models_key", columnList = "model_key", unique = true),
        @Index(name = "idx_llm_models_provider", columnList = "provider_id")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "model_type", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter
@ToString(exclude = "provider")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LlmModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "model_key", nullable = false, unique = true, length = 100)
    private String modelKey;

    @Column(name = "model_name", nullable = false, length = 150)
    private String modelName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", nullable = false,
            referencedColumnName = "provider_id",
            foreignKey = @ForeignKey(name = "FK_llm_models_provider_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LlmProvider provider;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "capabilities", columnDefinition = "jsonb")
    private List<String> capabilities = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_config", columnDefinition = "jsonb")
    private Map<String, Object> extraConfig = new HashMap<>();

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected LlmModel(String modelKey, String modelName, List<String> capabilities, Map<String, Object> extraConfig) {
        if (modelKey == null || modelKey.isBlank()) {
            throw new IllegalArgumentException("Model key is required");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("Model name is required");
        }
        Instant now = Instant.now();
        this.modelKey = modelKey.strip();
        this.modelName = modelName.strip();
        this.capabilities = capabilities != null ? new ArrayList<>(capabilities) : new ArrayList<>();
        this.extraConfig = extraConfig != null ? new HashMap<>(extraConfig) : new HashMap<>();
        this.enabled = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public abstract ModelType getType();

    /** Called by {@link LlmProvider#registerModel(LlmModel)}. */
    public void assignTo(LlmProvider provider) {
        this.provider = provider;
    }

    public List<String> getCapabilities() {
        return capabilities != null ? Collections.unmodifiableList(capabilities) : List.of();
    }

    public Map<String, Object> getExtraConfig() {
        return extraConfig != null ? Collections.unmodifiableMap(extraConfig) : Map.of();
    }

    public boolean hasCapability(ModelCapability capability) {
        return capabilities != null && capability != null && capabilities.contains(capability.getValue());
    }

    public boolean hasAllCapabilities(ModelCapability... required) {
        if (required == null) {
            return true;
        }
        for (ModelCapability cap : required) {
            if (!hasCapability(cap)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAnyCapability(ModelCapability... required) {
        if (required == null) {
            return false;
        }
        for (ModelCapability cap : required) {
            if (hasCapability(cap)) {
                return true;
            }
        }
        return false;
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
