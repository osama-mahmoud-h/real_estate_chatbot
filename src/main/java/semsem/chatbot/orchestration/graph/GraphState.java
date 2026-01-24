package semsem.chatbot.orchestration.graph;

import semsem.chatbot.orchestration.graph.output.*;

import java.util.List;

/**
 * Base interface for graph state.
 * Defines typed fields that nodes read/write during execution.
 * Each node has its own typed output class - no string keys, full compile-time safety.
 */
public interface GraphState {

    // ==================== Input Fields ====================
    Long getConversationId();
    void setConversationId(Long conversationId);

    String getUserQuery();
    void setUserQuery(String userQuery);

    List<ChatMessage> getMessages();
    void setMessages(List<ChatMessage> messages);
    void addMessage(ChatMessage message);

    // ==================== Node Outputs (Typed) ====================

    LanguageDetectorOutput getLanguageDetectorOutput();
    void setLanguageDetectorOutput(LanguageDetectorOutput output);

    QueryAnalyzerOutput getEntityExtractorOutput();  // Combined intent + entity extraction
    void setEntityExtractorOutput(QueryAnalyzerOutput output);

    RagRetrieverOutput getRagRetrieverOutput();
    void setRagRetrieverOutput(RagRetrieverOutput output);

    SqlGeneratorOutput getSqlGeneratorOutput();
    void setSqlGeneratorOutput(SqlGeneratorOutput output);

    SqlExecutorOutput getSqlExecutorOutput();
    void setSqlExecutorOutput(SqlExecutorOutput output);

    ResponseGeneratorOutput getResponseGeneratorOutput();
    void setResponseGeneratorOutput(ResponseGeneratorOutput output);

    FinalResponseBuilderOutput getFinalResponseBuilderOutput();
    void setFinalResponseBuilderOutput(FinalResponseBuilderOutput output);

    // ==================== Final Response ====================
    String getResponse();
    void setResponse(String response);

    // ==================== Metadata ====================
    StateMetadata getMetadata();
    void setMetadata(StateMetadata metadata);
}