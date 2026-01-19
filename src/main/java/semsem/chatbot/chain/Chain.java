package semsem.chatbot.chain;

import semsem.chatbot.model.enums.ChainNames;

/**
 * Base interface for all chains.
 * Chains are composable units of LLM operations with typed input/output.
 *
 * @param <I> Input type extending ChainInput
 * @param <O> Output type extending ChainOutput
 */
public interface Chain<I extends ChainInput, O extends ChainOutput> {

    // ==================== Identity ====================

    /**
     * Returns the type-safe name of this chain.
     */
    ChainNames getName();

    // ==================== Execution ====================

    /**
     * Executes the chain with typed input and returns typed output.
     *
     * @param input the chain input
     * @return the chain output with result and metadata
     */
    O invoke(I input);

    // ==================== Configuration ====================

    /**
     * Configures the chain with memory for stateful conversations.
     *
     * @param memory the memory instance
     * @return this chain for fluent configuration
     */
    Chain<I, O> withMemory(ChainMemory memory);

    /**
     * Configures the chain with callbacks for execution hooks.
     *
     * @param callbacks the callbacks instance
     * @return this chain for fluent configuration
     */
    Chain<I, O> withCallbacks(ChainCallbacks callbacks);
}