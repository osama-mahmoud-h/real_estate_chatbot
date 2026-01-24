package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.output.LanguageDetectorOutput;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.node.GraphNode;
import semsem.chatbot.service.analysis.EntityExtractionService;

import java.util.Optional;

/**
 * Entity Extractor Node - Extracts intent and entities from user query.
 * Delegates to EntityExtractionService for the actual logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityExtractorNode implements GraphNode<ChatGraphState> {

    private static final String DEFAULT_LANGUAGE = "English";

    private final EntityExtractionService entityExtractionService;

    @Override
    public GraphNodeNames getName() {
        return GraphNodeNames.ENTITY_EXTRACTOR;
    }

    @Override
    public ChatGraphState execute(ChatGraphState state) {
        log.debug("Executing EntityExtractorNode for query: {}", state.getUserQuery());

        String detectedLanguage = Optional.ofNullable(state.getLanguageDetectorOutput())
                .map(LanguageDetectorOutput::getDetectedLanguage)
                .orElse(DEFAULT_LANGUAGE);

        QueryAnalyzerOutput output = entityExtractionService.extract(
                state.getUserQuery(),
                detectedLanguage,
                state.getMessages()
        );

        state.setEntityExtractorOutput(output);

        return state;
    }
}
