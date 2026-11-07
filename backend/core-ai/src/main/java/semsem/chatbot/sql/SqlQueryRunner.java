package semsem.chatbot.sql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.dbchat.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlGeneratorOutput;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlQueryRunner {

    private static final int MAX_RESULTS = 100;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SqlExecutorOutput run(SqlGeneratorOutput sqlOutput) {
        if (sqlOutput == null || !sqlOutput.isExecutable()) {
            String reason = skipReason(sqlOutput);
            log.debug("Skipping SQL execution: {}", reason);
            return SqlExecutorOutput.skipped(reason);
        }

        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> params = Optional.ofNullable(sqlOutput.getParameters())
                    .orElse(Collections.emptyMap());

            log.debug("Executing generated SQL on external database: sql='{}', params={}",
                    sqlOutput.getGeneratedSql(), params);

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlOutput.getGeneratedSql(), params);
            if (results.size() > MAX_RESULTS) {
                log.warn("SQL query returned {} rows; truncating to MAX_RESULTS={}", results.size(), MAX_RESULTS);
                results = results.subList(0, MAX_RESULTS);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Query executed successfully: {} rows in {}ms", results.size(), executionTime);
            return SqlExecutorOutput.success(results, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("SQL execution failed after {}ms [sql='{}']: {}",
                    executionTime, sqlOutput.getGeneratedSql(), e.getMessage(), e);
            return SqlExecutorOutput.failure(e.getMessage(), executionTime);
        }
    }

    private String skipReason(SqlGeneratorOutput sqlOutput) {
        return Optional.ofNullable(sqlOutput)
                .map(SqlGeneratorOutput::getExplanation)
                .orElse("No SQL to execute");
    }
}