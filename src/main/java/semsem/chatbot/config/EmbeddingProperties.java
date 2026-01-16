package semsem.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Embedding providers.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingProperties {

    private String defaultProvider = "cohere";
    private GeminiEmbeddingProperties gemini = new GeminiEmbeddingProperties();
    private CohereEmbeddingProperties cohere = new CohereEmbeddingProperties();
    private OllamaEmbeddingProperties ollama = new OllamaEmbeddingProperties();

    @Data
    public static class GeminiEmbeddingProperties {
        private String apiKey;
        private String model = "text-embedding-004";
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private int dimensions = 768;
    }

    @Data
    public static class CohereEmbeddingProperties {
        private String apiKey;
        private String model = "embed-english-v3.0";
        private String baseUrl = "https://api.cohere.ai";
        private int dimensions = 1024;
    }

    @Data
    public static class OllamaEmbeddingProperties {
        private String baseUrl = "http://localhost:11434";
        private String model = "nomic-embed-text";
        private int dimensions = 768;
    }
}
