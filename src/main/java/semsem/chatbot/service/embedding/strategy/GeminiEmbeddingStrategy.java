package semsem.chatbot.service.embedding.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.config.AIProperties;
import semsem.chatbot.config.ai.ModelConfig;
import semsem.chatbot.config.ai.ProviderConfig;
import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.List;

/**
 * Google Gemini Embedding Strategy.
 * Single Responsibility: knows only how to call Gemini Embedding API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiEmbeddingStrategy extends BaseEmbeddingStrategy {

    private final AIProperties aiProperties;

    private ProviderConfig getProviderConfig() {
        return aiProperties.getProviderOrThrow("gemini");
    }

    private ModelConfig getModelConfig() {
        String modelName = aiProperties.getEmbedding().getModel();
        return getProviderConfig().getModel(modelName).orElse(null);
    }

    @Override
    public float[] embed(String text) {
        var config = getProviderConfig();
        var model = getModelConfig();
        int dims = model != null ? model.getDimensions() : 768;
        log.debug("Embedding with Gemini model: {}, baseUrl: {}",
                aiProperties.getEmbedding().getModel(), config.getBaseUrl());
        // TODO: Implement using Google Gemini Embedding API
        return new float[dims];
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        log.debug("Batch embedding {} texts with Gemini model: {}",
                texts.size(), aiProperties.getEmbedding().getModel());
        return texts.stream().map(this::embed).toList();
    }

    @Override
    public int getDimensions() {
        var model = getModelConfig();
        return model != null ? model.getDimensions() : 768;
    }

    @Override
    public EmbeddingProvider getProvider() {
        return EmbeddingProvider.GEMINI;
    }

    @Override
    public String getModelName() {
        return aiProperties.getEmbedding().getModel();
    }

    @Override
    public boolean isAvailable() {
        return aiProperties.getProvider("gemini")
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }
}