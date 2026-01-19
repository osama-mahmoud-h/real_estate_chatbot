package semsem.chatbot.chain;

import semsem.chatbot.model.enums.ChainNames;

import java.time.Instant;
import java.util.Map;

/**
 * Base interface for chain output.
 * Implementations define typed fields for specific chain outputs.
 * Provides execution result, metadata, and error handling.
 */
public interface ChainOutput {

    // ==================== Result ====================

    /**
     * Returns the primary output/response from the chain.
     */
    String getOutput();

    /**
     * Returns whether the chain executed successfully.
     */
    boolean isSuccess();

    // ==================== Error Handling ====================

    /**
     * Returns the error message if execution failed.
     */
    String getErrorMessage();

    /**
     * Returns the exception if one occurred.
     */
    Exception getException();

    // ==================== Metrics ====================

    /**
     * Returns the execution latency in milliseconds.
     */
    long getLatencyMs();

    /**
     * Returns the number of tokens used (for LLM chains).
     */
    int getTokensUsed();

    /**
     * Returns the execution start time.
     */
    Instant getStartTime();

    /**
     * Returns the execution end time.
     */
    Instant getEndTime();

    // ==================== Metadata ====================

    /**
     * Returns additional output metadata.
     */
    Map<String, Object> getMetadata();

    /**
     * Returns the chain name that produced this output.
     */
    ChainNames getChainName();
}