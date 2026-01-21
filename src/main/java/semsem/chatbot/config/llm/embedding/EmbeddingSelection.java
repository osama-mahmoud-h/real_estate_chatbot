package semsem.chatbot.config.llm.embedding;

import lombok.Data;
import semsem.chatbot.config.llm.ISelection;

/**
 * Embedding model selection configuration.
 * Specifies which provider and model to use for embeddings.
 * Implements ISelection interface.
 */
@Data
public class EmbeddingSelection implements ISelection {

    /** Provider name: gemini | cohere | ollama */
    private String provider = "ollama";

    /** Model name from the chosen provider */
    private String model = "nomic-embed-text";
}