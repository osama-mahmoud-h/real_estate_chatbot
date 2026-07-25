package semsem.chatbot.orchestration.dbchat;

import semsem.chatbot.orchestration.common.GraphOrchestrator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.dbchat.state.DbChatNodes;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;
import semsem.chatbot.orchestration.dbchat.node.EntityExtractorNode;
import semsem.chatbot.orchestration.dbchat.node.FinalResponseBuilderNode;
import semsem.chatbot.orchestration.dbchat.node.SqlExecutorNode;
import semsem.chatbot.orchestration.dbchat.node.SqlGeneratorNode;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
@Component
public class DbChatGraph implements GraphOrchestrator<DbChatState> {

    @Getter
    private final CompiledGraph<DbChatState> compiledGraph;

    public DbChatGraph(EntityExtractorNode entityExtractorNode,
                     SqlGeneratorNode sqlGeneratorNode,
                     SqlExecutorNode sqlExecutorNode,
                     FinalResponseBuilderNode finalResponseBuilderNode,
                     BaseCheckpointSaver checkpointSaver) throws GraphStateException {

        StateGraph<DbChatState> graph = new StateGraph<>(DbChatState.SCHEMA, DbChatState::new);
        registerNodes(graph, entityExtractorNode, sqlGeneratorNode, sqlExecutorNode, finalResponseBuilderNode);
        wireEdges(graph);

        this.compiledGraph = graph.compile(CompileConfig.builder()
                .checkpointSaver(checkpointSaver)
                .build());

        log.info("DbChatGraph compiled: START -> {} -> {} -> {} -> {} -> END",
                DbChatNodes.ENTITY_EXTRACTOR, DbChatNodes.SQL_GENERATOR,
                DbChatNodes.SQL_EXECUTOR, DbChatNodes.FINAL_RESPONSE);
    }

    private void registerNodes(StateGraph<DbChatState> graph,
                               EntityExtractorNode entityExtractor,
                               SqlGeneratorNode sqlGenerator,
                               SqlExecutorNode sqlExecutor,
                               FinalResponseBuilderNode finalResponse) throws GraphStateException {
        graph.addNode(DbChatNodes.ENTITY_EXTRACTOR, node_async(entityExtractor))
                .addNode(DbChatNodes.SQL_GENERATOR, node_async(sqlGenerator))
                .addNode(DbChatNodes.SQL_EXECUTOR, node_async(sqlExecutor))
                .addNode(DbChatNodes.FINAL_RESPONSE, node_async(finalResponse));
    }

    private void wireEdges(StateGraph<DbChatState> graph) throws GraphStateException {
        graph.addEdge(START, DbChatNodes.ENTITY_EXTRACTOR)
                .addEdge(DbChatNodes.ENTITY_EXTRACTOR, DbChatNodes.SQL_GENERATOR)
                .addEdge(DbChatNodes.SQL_GENERATOR, DbChatNodes.SQL_EXECUTOR)
                .addEdge(DbChatNodes.SQL_EXECUTOR, DbChatNodes.FINAL_RESPONSE)
                .addEdge(DbChatNodes.FINAL_RESPONSE, END);
    }

    @Override
    public DbChatState run(Map<String, Object> input, String threadId) {
        RunnableConfig config = RunnableConfig.builder().threadId(threadId).build();
        return compiledGraph.invoke(input, config)
                .orElseThrow(() -> new IllegalStateException("Chat graph returned no final state"));
    }
}