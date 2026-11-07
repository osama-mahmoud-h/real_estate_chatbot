package semsem.chatbot.llm.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain entity assigning a specific {@link ChatLlmModel} (and optional parameter overrides)
 * to a given {@code (flowName, stepName)} pair, e.g. {@code ("db_chat", "entity_extractor") -> "openrouter_gemma26b"}.
 */
@Entity
@Table(name = "llm_flow_step_configs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_flow_step_name", columnNames = {"flow_name", "step_name"})
})
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlowStepLlmConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    @Column(name = "flow_name", nullable = false, length = 100)
    private String flowName;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "model_id", nullable = false,
            referencedColumnName = "model_id",
            foreignKey = @ForeignKey(name = "FK_flow_step_model_id"))
    private ChatLlmModel chatModel;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "maxTokens", column = @Column(name = "max_tokens_override")),
            @AttributeOverride(name = "temperature", column = @Column(name = "temperature_override")),
            @AttributeOverride(name = "topP", column = @Column(name = "top_p_override"))
    })
    private GenerationParameters parameterOverrides = GenerationParameters.none();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_config", columnDefinition = "jsonb")
    private Map<String, Object> extraConfig = new HashMap<>();

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private FlowStepLlmConfig(String flowName,
                              String stepName,
                              ChatLlmModel chatModel,
                              GenerationParameters parameterOverrides,
                              Map<String, Object> extraConfig) {
        if (flowName == null || flowName.isBlank()) {
            throw new IllegalArgumentException("Flow name is required");
        }
        if (stepName == null || stepName.isBlank()) {
            throw new IllegalArgumentException("Step name is required");
        }
        if (chatModel == null) {
            throw new IllegalArgumentException("A flow step configuration requires a ChatLlmModel");
        }
        Instant now = Instant.now();
        this.flowName = flowName.strip();
        this.stepName = stepName.strip();
        this.chatModel = chatModel;
        this.parameterOverrides = parameterOverrides != null ? parameterOverrides : GenerationParameters.none();
        this.extraConfig = extraConfig != null ? new HashMap<>(extraConfig) : new HashMap<>();
        this.enabled = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static FlowStepLlmConfig assign(String flowName, String stepName, ChatLlmModel chatModel) {
        return new FlowStepLlmConfig(flowName, stepName, chatModel, GenerationParameters.none(), Map.of());
    }

    public static FlowStepLlmConfig assignWithOverrides(String flowName,
                                                        String stepName,
                                                        ChatLlmModel chatModel,
                                                        GenerationParameters overrides,
                                                        Map<String, Object> extraConfig) {
        return new FlowStepLlmConfig(flowName, stepName, chatModel, overrides, extraConfig);
    }

    public void reassignModel(ChatLlmModel newModel) {
        if (newModel == null) {
            throw new IllegalArgumentException("ChatLlmModel cannot be null");
        }
        this.chatModel = newModel;
        touch();
    }

    public void overrideParameters(GenerationParameters overrides) {
        this.parameterOverrides = overrides != null ? overrides : GenerationParameters.none();
        touch();
    }

    public void configureExtra(Map<String, Object> extraConfig) {
        this.extraConfig = extraConfig != null ? new HashMap<>(extraConfig) : new HashMap<>();
        touch();
    }

    /**
     * Computes effective generation parameters by merging step overrides onto the model's defaults.
     */
    public GenerationParameters effectiveParameters() {
        return chatModel.getGenerationParameters().mergeOverride(parameterOverrides);
    }

    /**
     * Computes effective extra configuration by overlaying step extraConfig onto the model's extraConfig.
     */
    public Map<String, Object> effectiveExtraConfig() {
        Map<String, Object> merged = new HashMap<>(chatModel.getExtraConfig());
        if (extraConfig != null) {
            merged.putAll(extraConfig);
        }
        return Collections.unmodifiableMap(merged);
    }

    public void enable() {
        this.enabled = true;
        touch();
    }

    public void disable() {
        this.enabled = false;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
