package semsem.chatbot.llm.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Value object encapsulating runtime generation parameters (max_tokens, temperature, top_p).
 * Supports clean merging of step-level overrides on top of model-level defaults.
 */
@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GenerationParameters {

    public static final int DEFAULT_MAX_TOKENS = 4096;
    public static final double DEFAULT_TEMPERATURE = 0.0;
    public static final double DEFAULT_TOP_P = 0.95;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "top_p")
    private Double topP;

    public static GenerationParameters defaults() {
        return new GenerationParameters(DEFAULT_MAX_TOKENS, DEFAULT_TEMPERATURE, DEFAULT_TOP_P);
    }

    public static GenerationParameters none() {
        return new GenerationParameters(null, null, null);
    }

    public static GenerationParameters of(Integer maxTokens, Double temperature, Double topP) {
        return new GenerationParameters(maxTokens, temperature, topP);
    }

    /**
     * Merges an override onto these base parameters. Non-null fields in {@code override} win.
     */
    public GenerationParameters mergeOverride(GenerationParameters override) {
        if (override == null) {
            return this;
        }
        return new GenerationParameters(
                override.maxTokens != null ? override.maxTokens : this.maxTokens,
                override.temperature != null ? override.temperature : this.temperature,
                override.topP != null ? override.topP : this.topP
        );
    }

    public int maxTokensOrDefault() {
        return maxTokens != null ? maxTokens : DEFAULT_MAX_TOKENS;
    }

    public double temperatureOrDefault() {
        return temperature != null ? temperature : DEFAULT_TEMPERATURE;
    }

    public double topPOrDefault() {
        return topP != null ? topP : DEFAULT_TOP_P;
    }
}
