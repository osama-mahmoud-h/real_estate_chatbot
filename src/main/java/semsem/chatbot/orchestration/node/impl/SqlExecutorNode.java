package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.node.GraphNode;
import semsem.chatbot.service.sql.SqlExecutorService;

import java.util.Collections;
import java.util.Optional;

/**
 * SQL Executor Node - Executes safe, read-only SQL queries.
 * Delegates to SqlExecutorService for the actual logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlExecutorNode implements GraphNode<ChatGraphState> {

    private final SqlExecutorService sqlExecutorService;

    @Override
    public GraphNodeNames getName() {
        return GraphNodeNames.SQL_EXECUTOR;
    }

    @Override
    public ChatGraphState execute(ChatGraphState state) {
        log.debug("Executing SqlExecutorNode");

        SqlExecutorOutput output = Optional.ofNullable(state.getSqlGeneratorOutput())
                .map(sqlExecutorService::execute)
                .orElseGet(this::createSkippedOutput);

        state.setSqlExecutorOutput(output);

        return state;
    }

    private SqlExecutorOutput createSkippedOutput() {
        return SqlExecutorOutput.builder()
                .results(Collections.emptyList())
                .rowCount(0)
                .executionTimeMs(0)
                .success(true)
                .errorMessage("Skipped: No SQL to execute")
                .build();
    }
}