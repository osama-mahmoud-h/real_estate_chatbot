package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.service.analysis.EntityExtractionService;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityExtractorNode implements NodeAction<ChatState> {

    private static final String DEFAULT_LANGUAGE = "English";

    private final EntityExtractionService entityExtractionService;

    @Override
    public Map<String, Object> apply(ChatState state) {
        QueryAnalyzerOutput output = entityExtractionService.extract(
                state.userQuery(), DEFAULT_LANGUAGE, state.messages());
        return Map.of(ChatState.Keys.ANALYSIS, output);
    }
}