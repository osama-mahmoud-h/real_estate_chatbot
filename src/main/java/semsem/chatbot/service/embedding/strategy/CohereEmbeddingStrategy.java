package semsem.chatbot.service.embedding.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.config.LLMProperties;
import semsem.chatbot.config.llm.embedding.EmbeddingModelConfig;
import semsem.chatbot.config.llm.ProviderConfig;
import semsem.chatbot.model.enums.EmbeddingProvider;

import java.util.List;

/**
 * Cohere Embedding Strategy.
 * Single Responsibility: knows only how to call Cohere Embedding API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CohereEmbeddingStrategy extends BaseEmbeddingStrategy {

    private final LLMProperties llmProperties;

    private ProviderConfig getProviderConfig() {
        return llmProperties.getProviderOrThrow("cohere");
    }

    private EmbeddingModelConfig getModelConfig() {
        String modelName = llmProperties.getEmbedding().getModel();
        return getProviderConfig().getEmbeddingModel(modelName).orElse(null);
    }

    @Override
    public float[] embed(String text) {
        var config = getProviderConfig();
        var model = getModelConfig();
        int dims = model != null ? model.getDimensions() : 1024;
        log.debug("Embedding with Cohere model: {}, baseUrl: {}",
                llmProperties.getEmbedding().getModel(), config.getBaseUrl());
        // TODO: Implement using Cohere Embedding API
        return new float[dims];
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        log.debug("Batch embedding {} texts with Cohere model: {}",
                texts.size(), llmProperties.getEmbedding().getModel());
        return texts.stream().map(this::embed).toList();
    }

    @Override
    public int getDimensions() {
        var model = getModelConfig();
        return model != null ? model.getDimensions() : 1024;
    }

    @Override
    public EmbeddingProvider getProvider() {
        return EmbeddingProvider.COHERE;
    }

    @Override
    public String getModelName() {
        return llmProperties.getEmbedding().getModel();
    }

    @Override
    public boolean isAvailable() {
        return llmProperties.getProvider("cohere")
                .map(ProviderConfig::isAvailable)
                .orElse(false);
    }
}