package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;
import semsem.chatbot.service.sql.SqlGeneratorService;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlGeneratorNode implements NodeAction<ChatState> {

    private final SqlGeneratorService sqlGeneratorService;

    @Override
    public Map<String, Object> apply(ChatState state) {
        SqlGeneratorOutput output = Optional.ofNullable(state.analysis())
                .map(analysis -> sqlGeneratorService.generate(
                        analysis.getIntent(), analysis.getEntities(), state.userQuery()))
                .orElseGet(this::skipped);
        return Map.of(ChatState.Keys.SQL_GENERATION, output);
    }

    private SqlGeneratorOutput skipped() {
        return SqlGeneratorOutput.builder()
                .isSafe(true)
                .explanation("Skipped: No analysis output available")
                .build();
    }
}