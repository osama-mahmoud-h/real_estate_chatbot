package semsem.chatbot.service.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Google GenAI embedding service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleEmbeddingService implements EmbeddingService {

    // TODO: Inject Google GenAI embedding client

    @Override
    public float[] embed(String text) {
        // TODO: Implement using Google GenAI
        return new float[0];
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        // TODO: Implement batch embedding
        return List.of();
    }

    @Override
    public int getDimensions() {
        return 768; // text-embedding-004
    }

    @Override
    public String getModelName() {
        return "text-embedding-004";
    }
}
