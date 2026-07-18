package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;
import semsem.chatbot.service.sql.SqlExecutorService;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlExecutorNode implements NodeAction<ChatState> {

    private final SqlExecutorService sqlExecutorService;

    @Override
    public Map<String, Object> apply(ChatState state) {
        SqlExecutorOutput output = Optional.ofNullable(state.sqlGeneration())
                .map(sqlExecutorService::execute)
                .orElseGet(this::skipped);
        return Map.of(ChatState.Keys.SQL_EXECUTION, output);
    }

    private SqlExecutorOutput skipped() {
        return SqlExecutorOutput.builder()
                .results(Collections.emptyList())
                .rowCount(0)
                .executionTimeMs(0)
                .success(true)
                .errorMessage("Skipped: No SQL to execute")
                .build();
    }
}