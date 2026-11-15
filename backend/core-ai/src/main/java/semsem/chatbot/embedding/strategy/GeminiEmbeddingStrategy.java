package semsem.chatbot.embedding.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.domain.entity.provider.LlmProvider;
import semsem.chatbot.llm.domain.entity.EmbeddingLlmModel;
import semsem.chatbot.llm.service.LlmModelResolver;

import java.util.List;
import java.util.Optional;

/**
 * Google Gemini Embedding Strategy backed by database provider/model configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiEmbeddingStrategy extends BaseEmbeddingStrategy {

    private final LlmModelResolver modelResolver;

    private Optional<EmbeddingLlmModel> resolveModel() {
        return modelResolver.findProvider(EmbeddingProvider.GEMINI.getKey())
                .flatMap(p -> p.getModels().stream()
                        .filter(EmbeddingLlmModel.class::isInstance)
                        .map(EmbeddingLlmModel.class::cast)
                        .findFirst());
    }

    @Override
    public float[] embed(String text) {
        int dims = getDimensions();
        log.debug("Embedding with Gemini model: {}, dims: {}", getModelName(), dims);
        return new float[dims];
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        return texts.stream().map(this::embed).toList();
    }

    @Override
    public int getDimensions() {
        try {
            return resolveModel().map(EmbeddingLlmModel::getDimensions).orElse(768);
        } catch (Exception e) {
            return 768;
        }
    }

    @Override
    public EmbeddingProvider getProvider() {
        return EmbeddingProvider.GEMINI;
    }

    @Override
    public String getModelName() {
        try {
            return resolveModel().map(EmbeddingLlmModel::getModelName).orElse("text-embedding-004");
        } catch (Exception e) {
            return "text-embedding-004";
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            return modelResolver.findProvider(EmbeddingProvider.GEMINI.getKey())
                    .map(LlmProvider::isAvailable)
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}
