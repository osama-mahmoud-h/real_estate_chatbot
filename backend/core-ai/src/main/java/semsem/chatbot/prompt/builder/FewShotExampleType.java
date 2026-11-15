package semsem.chatbot.prompt.builder;


/**
 * Types of few-shot examples stored in vector DB.
 */
public enum FewShotExampleType {
    SQL_QUERY,
    ENTITY_EXTRACTION,
    INTENT_CLASSIFICATION,
    RESPONSE_GENERATION
}
