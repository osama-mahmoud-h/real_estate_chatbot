package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.output.FinalResponseBuilderOutput;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.node.GraphNode;
import semsem.chatbot.service.response.ResponseGeneratorService;

import java.util.Optional;

/**
 * Final Response Builder Node - Generates the final natural language response.
 * Delegates to ResponseGeneratorService for the actual logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FinalResponseBuilderNode implements GraphNode<ChatGraphState> {

    private static final String DEFAULT_LANGUAGE = "English";

    private final ResponseGeneratorService responseGeneratorService;

    @Override
    public GraphNodeNames getName() {
        return GraphNodeNames.FINAL_RESPONSE_BUILDER;
    }

    @Override
    public ChatGraphState execute(ChatGraphState state) {
        log.debug("Executing FinalResponseBuilderNode");

        QueryAnalyzerOutput entityOutput = state.getEntityExtractorOutput();

        String detectedLanguage = Optional.ofNullable(state.getLanguageDetectorOutput())
                .map(output -> output.getDetectedLanguage())
                .orElse(DEFAULT_LANGUAGE);

        QueryAnalyzerOutput.IntentResult intent = Optional.ofNullable(entityOutput)
                .map(QueryAnalyzerOutput::getIntent)
                .orElse(null);

        QueryAnalyzerOutput.ExtractedEntities entities = Optional.ofNullable(entityOutput)
                .map(QueryAnalyzerOutput::getEntities)
                .orElse(null);

        String response = responseGeneratorService.generate(
                state.getUserQuery(),
                intent,
                entities,
                state.getSqlExecutorOutput(),
                detectedLanguage
        );

        // Set output
        state.setFinalResponseBuilderOutput(FinalResponseBuilderOutput.builder()
                .finalResponse(response)
                .build());

        // Set final response
        state.setResponse(response);

        // Add assistant message to conversation
        state.addMessage(ChatMessage.assistant(response));

        return state;
    }
}