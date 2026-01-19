package semsem.chatbot.chain;

import semsem.chatbot.model.enums.ChainNames;

/**
 * Base interface for chain execution callbacks.
 * Provides hooks for monitoring and logging chain execution.
 */
public interface ChainCallbacks {

    // ==================== Lifecycle Hooks ====================

    /**
     * Called before chain execution starts.
     *
     * @param chainName the name of the chain
     * @param input     the chain input
     */
    void onChainStart(ChainNames chainName, ChainInput input);

    /**
     * Called after chain execution completes successfully.
     *
     * @param chainName the name of the chain
     * @param output    the chain output
     */
    void onChainEnd(ChainNames chainName, ChainOutput output);

    /**
     * Called when chain execution fails.
     *
     * @param chainName the name of the chain
     * @param error     the exception that occurred
     */
    void onChainError(ChainNames chainName, Exception error);

    // ==================== LLM Hooks ====================

    /**
     * Called before an LLM call within the chain.
     *
     * @param prompt the prompt being sent to the LLM
     */
    void onLLMStart(String prompt);

    /**
     * Called after an LLM call completes.
     *
     * @param response the LLM response
     * @param tokens   the number of tokens used
     */
    void onLLMEnd(String response, int tokens);
}