package semsem.chatbot.service.sql;

import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;

/**
 * Service for executing safe, read-only SQL queries against the real estate database.
 */
public interface SqlExecutorService {

    /**
     * Execute a generated SQL query.
     *
     * @param sqlOutput the generated SQL output containing query and parameters
     * @return SqlExecutorOutput containing query results
     */
    SqlExecutorOutput execute(SqlGeneratorOutput sqlOutput);
}