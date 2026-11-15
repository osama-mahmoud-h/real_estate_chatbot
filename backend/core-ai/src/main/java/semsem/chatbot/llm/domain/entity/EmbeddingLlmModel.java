package semsem.chatbot.llm.domain.entity;

import semsem.chatbot.llm.domain.enums.EmbeddingModelCapability;
import semsem.chatbot.llm.domain.enums.ModelCapability;
import semsem.chatbot.llm.domain.enums.ModelType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Embedding model entity (e.g. "cohere_embed_multilingual_v3" -> "embed-multilingual-v3.0").
 * Holds vector dimensions and maximum input token limits.
 */
@Entity
@DiscriminatorValue("EMBEDDING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmbeddingLlmModel extends LlmModel {

    @Column(name = "dimensions")
    private Integer dimensions;

    @Column(name = "max_input_tokens")
    private Integer maxInputTokens;

    private EmbeddingLlmModel(String modelKey,
                              String modelName,
                              int dimensions,
                              int maxInputTokens,
                              List<String> capabilities,
                              Map<String, Object> extraConfig) {
        super(modelKey, modelName, capabilities, extraConfig);
        this.dimensions = dimensions;
        this.maxInputTokens = maxInputTokens;
    }

    public static EmbeddingLlmModel of(String modelKey,
                                       String modelName,
                                       int dimensions,
                                       int maxInputTokens,
                                       List<EmbeddingModelCapability> capabilities) {
        return of(modelKey, modelName, dimensions, maxInputTokens, capabilities, Map.of());
    }

    public static EmbeddingLlmModel of(String modelKey,
                                       String modelName,
                                       int dimensions,
                                       int maxInputTokens,
                                       List<EmbeddingModelCapability> capabilities,
                                       Map<String, Object> extraConfig) {
        List<String> capStrings = capabilities != null
                ? capabilities.stream().map(EmbeddingModelCapability::getValue).toList()
                : List.of();
        return new EmbeddingLlmModel(modelKey, modelName, dimensions, maxInputTokens, capStrings, extraConfig);
    }

    @Override
    public ModelType getType() {
        return ModelType.EMBEDDING;
    }

    public int getDimensions() {
        return dimensions != null ? dimensions : 0;
    }

    public int getMaxInputTokens() {
        return maxInputTokens != null ? maxInputTokens : 0;
    }

    public boolean supportsBatch() {
        return hasCapability(EmbeddingModelCapability.BATCH);
    }

    public boolean supportsMultilingual() {
        return hasCapability(EmbeddingModelCapability.MULTILINGUAL);
    }
}
