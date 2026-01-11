package semsem.chatbot.service.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Text embedding service with caching support.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TextEmbeddingService implements EmbeddingService {

    private final EmbeddingModelService embeddingModelService;
    // TODO: Add caching layer

    @Override
    public float[] embed(String text) {
        // TODO: Implement with caching
        return embeddingModelService.embed(text);
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        // TODO: Implement batch with caching
        return embeddingModelService.embed(texts);
    }

    @Override
    public int getDimensions() {
        return embeddingModelService.getDimensions();
    }

    @Override
    public String getModelName() {
        return embeddingModelService.getModelName();
    }
}
