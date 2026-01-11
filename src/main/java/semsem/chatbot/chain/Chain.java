package semsem.chatbot.chain;

import java.util.Map;

/**
 * Base interface for all chains.
 * Chains are composable units of LLM operations.
 */
public interface Chain {

    String getName();

    ChainResult invoke(Map<String, Object> inputs);

    Chain withMemory(Object memory);

    Chain withCallbacks(Object callbacks);
}
