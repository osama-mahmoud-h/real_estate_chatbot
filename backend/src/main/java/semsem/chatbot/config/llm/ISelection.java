package semsem.chatbot.config.llm;

import semsem.chatbot.model.enums.LLMProvider;

/**
 * Base interface for model selections.
 * Provides common properties for chat and embedding selections.
 */
public interface ISelection {

    /**
     * Get the selected provider.
     */
    LLMProvider getProvider();

    /**
     * Get the selected model name.
     */
    String getModel();
}