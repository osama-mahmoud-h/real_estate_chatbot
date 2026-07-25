package semsem.chatbot.model.enums;

/**
 * Enum defining all available chain types.
 * Provides type-safe chain identification similar to GraphNodeNames.
 */
public enum ChainNames {
    LLM_CHAIN,
    CONVERSATION_CHAIN,
    SEQUENTIAL_CHAIN,
    PARALLEL_CHAIN,
    RAG_CHAIN,
    RETRIEVAL_QA_CHAIN,
    ROUTER_CHAIN,
    SUMMARIZATION_CHAIN,
    SQL_CHAIN,
    TRANSFORM_CHAIN
}