package semsem.chatbot.config.llm.chat;

import lombok.Data;
import semsem.chatbot.config.llm.ISelection;
import semsem.chatbot.model.enums.LLMProvider;

/**
 * Chat model selection configuration.
 * Specifies which provider and model to use for chat.
 * Implements ISelection interface.
 */
@Data
public class ChatSelection implements ISelection {

    /** Provider name: gemini | cohere | ollama */
    private LLMProvider provider = LLMProvider.OLLAMA;

    /** Model name from the chosen provider */
    private String model = "llama3.2";

    /** Generation temperature (0.0 - 1.0) */
    private double temperature = 0.0;

    /** Maximum tokens to generate */
    private int maxTokens = 4096;

    /** Top-p sampling */
    private double topP = 0.95;
}