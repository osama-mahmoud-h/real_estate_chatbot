package semsem.chatbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for LLM providers.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {

    private ChatProperties chat = new ChatProperties();
    private GeminiProperties gemini = new GeminiProperties();
    private CohereProperties cohere = new CohereProperties();
    private OllamaProperties ollama = new OllamaProperties();

    @Data
    public static class ChatProperties {
        private String defaultProvider = "gemini";
    }

    @Data
    public static class GeminiProperties {
        private String apiKey;
        private String model = "gemini-pro";
        private String baseUrl = "https://generativelanguage.googleapis.com";
        private double temperature = 0.7;
        private int maxTokens = 2048;
    }

    @Data
    public static class CohereProperties {
        private String apiKey;
        private String model = "command-r-plus";
        private String baseUrl = "https://api.cohere.ai";
        private double temperature = 0.7;
        private int maxTokens = 2048;
    }

    @Data
    public static class OllamaProperties {
        private String baseUrl = "http://localhost:11434";
        private ChatModelProperties chat = new ChatModelProperties();

        @Data
        public static class ChatModelProperties {
            private String model = "llama3";
            private double temperature = 0.7;
            private int maxTokens = 2048;
        }
    }
}
