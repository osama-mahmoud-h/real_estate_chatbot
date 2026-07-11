package semsem.chatbot.service.response;

import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;

/**
 * Service for generating natural language responses from query results.
 */
public interface ResponseGeneratorService {

    /**
     * Generate a natural language response from SQL query results.
     *
     * @param userQuery        original user query
     * @param intent           classified intent
     * @param entities         extracted entities
     * @param sqlResults       SQL query results
     * @param detectedLanguage language to respond in
     * @return generated natural language response
     */
    String generate(String userQuery,
                    QueryAnalyzerOutput.IntentResult intent,
                    QueryAnalyzerOutput.ExtractedEntities entities,
                    SqlExecutorOutput sqlResults,
                    String detectedLanguage);
}