package semsem.chatbot.config.ai;

import lombok.Data;

/**
 * Embedding model selection configuration.
 * Specifies which provider and model to use for embeddings.
 */
@Data
public class EmbeddingSelection {

    /** Provider name: gemini | cohere | ollama */
    private String provider = "ollama";

    /** Model name from the chosen provider */
    private String model = "nomic-embed-text";
}