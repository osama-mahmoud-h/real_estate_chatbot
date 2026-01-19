package semsem.chatbot.config.ai;

import lombok.Data;

/**
 * Chat model selection configuration.
 * Specifies which provider and model to use for chat.
 */
@Data
public class ChatSelection {

    /** Provider name: gemini | cohere | ollama */
    private String provider = "ollama";

    /** Model name from the chosen provider */
    private String model = "llama3.2";

    /** Generation temperature (0.0 - 1.0) */
    private double temperature = 0.0;

    /** Maximum tokens to generate */
    private int maxTokens = 4096;

    /** Top-p sampling */
    private double topP = 0.95;
}