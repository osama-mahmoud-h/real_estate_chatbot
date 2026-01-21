package semsem.chatbot.config.llm;

/**
 * Base interface for model selections.
 * Provides common properties for chat and embedding selections.
 */
public interface ISelection {

    /**
     * Get the selected provider name.
     */
    String getProvider();

    /**
     * Get the selected model name.
     */
    String getModel();
}