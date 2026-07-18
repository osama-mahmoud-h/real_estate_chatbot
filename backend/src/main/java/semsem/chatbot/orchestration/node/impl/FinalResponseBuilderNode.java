package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.service.response.ResponseGeneratorService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalResponseBuilderNode implements NodeAction<ChatState> {

    private static final String DEFAULT_LANGUAGE = "English";

    private final ResponseGeneratorService responseGeneratorService;

    @Override
    public Map<String, Object> apply(ChatState state) {
        QueryAnalyzerOutput analysis = state.analysis();

        String response = responseGeneratorService.generate(
                state.userQuery(),
                Optional.ofNullable(analysis).map(QueryAnalyzerOutput::getIntent).orElse(null),
                Optional.ofNullable(analysis).map(QueryAnalyzerOutput::getEntities).orElse(null),
                state.sqlExecution(),
                DEFAULT_LANGUAGE);

        return Map.of(
                ChatState.Keys.RESPONSE, response,
                ChatState.Keys.MESSAGES, List.of(ChatMessage.assistant(response)));
    }
}