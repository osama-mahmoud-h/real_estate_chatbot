package semsem.chatbot.orchestration.workflow;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.checkpointer.Checkpointer;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.graph.CompiledGraph;
import semsem.chatbot.orchestration.graph.StateGraph;
import semsem.chatbot.orchestration.node.impl.*;

/**
 * Main chat workflow definition.
 *
 * Flow: LANGUAGE_DETECTOR -> ENTITY_EXTRACTOR -> SQL_GENERATOR
 *       -> SQL_EXECUTOR -> FINAL_RESPONSE_BUILDER -> CHAT_HISTORY_BUILDER -> END
 */
@Slf4j
@Component
public class ChatWorkflow {

    private final LanguageDetectorNode languageDetectorNode;
    private final EntityExtractorNode entityExtractorNode;
    private final SqlGeneratorNode sqlGeneratorNode;
    private final SqlExecutorNode sqlExecutorNode;
    private final FinalResponseBuilderNode finalResponseBuilderNode;
    private final ChatHistoryBuilderNode chatHistoryBuilderNode;
    private final Checkpointer checkpointer;

    public ChatWorkflow(
            LanguageDetectorNode languageDetectorNode,
            EntityExtractorNode entityExtractorNode,
            SqlGeneratorNode sqlGeneratorNode,
            SqlExecutorNode sqlExecutorNode,
            FinalResponseBuilderNode finalResponseBuilderNode,
            ChatHistoryBuilderNode chatHistoryBuilderNode,
            @Qualifier("inMemoryCheckpointer") Checkpointer checkpointer) {
        this.languageDetectorNode = languageDetectorNode;
        this.entityExtractorNode = entityExtractorNode;
        this.sqlGeneratorNode = sqlGeneratorNode;
        this.sqlExecutorNode = sqlExecutorNode;
        this.finalResponseBuilderNode = finalResponseBuilderNode;
        this.chatHistoryBuilderNode = chatHistoryBuilderNode;
        this.checkpointer = checkpointer;
    }

    @Getter
    private CompiledGraph<ChatGraphState> compiledGraph;

    @PostConstruct
    public void init() {
        this.compiledGraph = buildGraph();
        log.info("ChatWorkflow initialized with flow: LANGUAGE_DETECTOR -> ENTITY_EXTRACTOR -> SQL_GENERATOR -> SQL_EXECUTOR -> FINAL_RESPONSE_BUILDER -> CHAT_HISTORY_BUILDER -> END");
    }

    private CompiledGraph<ChatGraphState> buildGraph() {
        StateGraph<ChatGraphState> graph = new StateGraph<>();

        // Add nodes
        graph.addNode(GraphNodeNames.LANGUAGE_DETECTOR, languageDetectorNode);
        graph.addNode(GraphNodeNames.ENTITY_EXTRACTOR, entityExtractorNode);
        graph.addNode(GraphNodeNames.SQL_GENERATOR, sqlGeneratorNode);
        graph.addNode(GraphNodeNames.SQL_EXECUTOR, sqlExecutorNode);
        graph.addNode(GraphNodeNames.FINAL_RESPONSE_BUILDER, finalResponseBuilderNode);
        graph.addNode(GraphNodeNames.CHAT_HISTORY_BUILDER, chatHistoryBuilderNode);

        // Define edges (flow path)
        graph.addEdge(GraphNodeNames.LANGUAGE_DETECTOR, GraphNodeNames.ENTITY_EXTRACTOR);
        graph.addEdge(GraphNodeNames.ENTITY_EXTRACTOR, GraphNodeNames.SQL_GENERATOR);
        graph.addEdge(GraphNodeNames.SQL_GENERATOR, GraphNodeNames.SQL_EXECUTOR);
        graph.addEdge(GraphNodeNames.SQL_EXECUTOR, GraphNodeNames.FINAL_RESPONSE_BUILDER);
        graph.addEdge(GraphNodeNames.FINAL_RESPONSE_BUILDER, GraphNodeNames.CHAT_HISTORY_BUILDER);

        // Set entry and finish points
        graph.setEntryPoint(GraphNodeNames.LANGUAGE_DETECTOR);
        graph.setFinishPoint(GraphNodeNames.CHAT_HISTORY_BUILDER);

        return graph.compile(checkpointer);
    }

    public ChatGraphState execute(ChatGraphState initialState) {
        log.debug("Executing chat workflow for conversation: {}", initialState.getConversationId());
        return compiledGraph.invoke(initialState);
    }
}
