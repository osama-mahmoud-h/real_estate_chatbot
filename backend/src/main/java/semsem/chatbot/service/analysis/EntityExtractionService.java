package semsem.chatbot.service.analysis;

import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;

import java.util.List;

/**
 * Service for extracting intent and entities from user queries.
 */
public interface EntityExtractionService {

    /**
     * Extract intent and entities from a user query.
     *
     * @param userQuery           the user's query
     * @param detectedLanguage    the detected language of the query
     * @param conversationHistory previous messages for context
     * @return QueryAnalyzerOutput containing intent and entities
     */
    QueryAnalyzerOutput extract(String userQuery, String detectedLanguage, List<ChatMessage> conversationHistory);

    /**
     * Extract with default language (English).
     */
    default QueryAnalyzerOutput extract(String userQuery, List<ChatMessage> conversationHistory) {
        return extract(userQuery, "English", conversationHistory);
    }
}