package semsem.chatbot.service.sql;

import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;

/**
 * Service for generating safe, read-only SQL queries from extracted entities.
 */
public interface SqlGeneratorService {

    /**
     * Generate a SQL query based on intent and extracted entities.
     *
     * @param intent   the classified user intent
     * @param entities the extracted entities
     * @param userQuery original user query for context
     * @return SqlGeneratorOutput containing the generated SQL and parameters
     */
    SqlGeneratorOutput generate(QueryAnalyzerOutput.IntentResult intent,
                                 QueryAnalyzerOutput.ExtractedEntities entities,
                                 String userQuery);

    /**
     * Get the database schema description for prompt context.
     */
    String getSchemaDescription();
}