package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;
import semsem.chatbot.orchestration.node.GraphNode;
import semsem.chatbot.service.sql.SqlGeneratorService;

import java.util.Optional;

/**
 * SQL Generator Node - Generates safe, read-only SQL queries.
 * Delegates to SqlGeneratorService for the actual logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlGeneratorNode implements GraphNode<ChatGraphState> {

    private final SqlGeneratorService sqlGeneratorService;

    @Override
    public GraphNodeNames getName() {
        return GraphNodeNames.SQL_GENERATOR;
    }

    @Override
    public ChatGraphState execute(ChatGraphState state) {
        log.debug("Executing SqlGeneratorNode");

        QueryAnalyzerOutput entityOutput = state.getEntityExtractorOutput();

        SqlGeneratorOutput output = Optional.ofNullable(entityOutput)
                .map(eo -> sqlGeneratorService.generate(
                        eo.getIntent(),
                        eo.getEntities(),
                        state.getUserQuery()))
                .orElseGet(this::createSkippedOutput);

        state.setSqlGeneratorOutput(output);

        return state;
    }

    private SqlGeneratorOutput createSkippedOutput() {
        return SqlGeneratorOutput.builder()
                .isSafe(true)
                .explanation("Skipped: No entity extraction output available")
                .build();
    }
}