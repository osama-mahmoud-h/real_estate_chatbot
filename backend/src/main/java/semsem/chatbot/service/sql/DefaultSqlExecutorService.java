package semsem.chatbot.service.sql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of SqlExecutorService.
 * Executes read-only queries against the real estate database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultSqlExecutorService implements SqlExecutorService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final int MAX_RESULTS = 100;

    @Override
    public SqlExecutorOutput execute(SqlGeneratorOutput sqlOutput) {
        // Validate input
        if (!isExecutable(sqlOutput)) {
            return createSkippedOutput(sqlOutput);
        }

        long startTime = System.currentTimeMillis();

        try {
            String sql = sqlOutput.getGeneratedSql();
            Map<String, Object> params = Optional.ofNullable(sqlOutput.getParameters())
                    .orElse(Collections.emptyMap());

            log.debug("Executing SQL: {}", sql);
            log.debug("With parameters: {}", params);

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);

            // Limit results
            if (results.size() > MAX_RESULTS) {
                results = results.subList(0, MAX_RESULTS);
            }

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Query executed successfully: {} rows in {}ms", results.size(), executionTime);

            return SqlExecutorOutput.builder()
                    .results(results)
                    .rowCount(results.size())
                    .executionTimeMs(executionTime)
                    .success(true)
                    .build();

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SQL execution failed: {}", e.getMessage(), e);

            return SqlExecutorOutput.builder()
                    .results(Collections.emptyList())
                    .rowCount(0)
                    .executionTimeMs(executionTime)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    private boolean isExecutable(SqlGeneratorOutput sqlOutput) {
        return Optional.ofNullable(sqlOutput)
                .map(SqlGeneratorOutput::isExecutable)
                .orElse(false);
    }

    private SqlExecutorOutput createSkippedOutput(SqlGeneratorOutput sqlOutput) {
        String reason = Optional.ofNullable(sqlOutput)
                .map(SqlGeneratorOutput::getExplanation)
                .orElse("No SQL to execute");

        return SqlExecutorOutput.builder()
                .results(Collections.emptyList())
                .rowCount(0)
                .executionTimeMs(0)
                .success(true)
                .errorMessage(reason)
                .build();
    }
}