package semsem.chatbot.model.enums;

public enum GraphNodeNames {
    LANGUAGE_DETECTOR,
    ENTITY_EXTRACTOR,       // Combined intent + entity extraction
    RAG_RETRIEVER,
    RESPONSE_GENERATOR,
    SQL_GENERATOR,
    SQL_EXECUTOR,
    FINAL_RESPONSE_BUILDER,
    CHAT_HISTORY_BUILDER
}
