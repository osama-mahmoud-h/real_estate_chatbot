package semsem.chatbot.orchestration.workflow;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.ChatNodes;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.node.impl.EntityExtractorNode;
import semsem.chatbot.orchestration.node.impl.FinalResponseBuilderNode;
import semsem.chatbot.orchestration.node.impl.SqlExecutorNode;
import semsem.chatbot.orchestration.node.impl.SqlGeneratorNode;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
@Component
public class ChatGraph implements GraphOrchestrator<ChatState> {

    @Getter
    private final CompiledGraph<ChatState> compiledGraph;

    public ChatGraph(EntityExtractorNode entityExtractorNode,
                     SqlGeneratorNode sqlGeneratorNode,
                     SqlExecutorNode sqlExecutorNode,
                     FinalResponseBuilderNode finalResponseBuilderNode,
                     BaseCheckpointSaver checkpointSaver) throws GraphStateException {

        StateGraph<ChatState> graph = new StateGraph<>(ChatState.SCHEMA, ChatState::new)
                .addNode(ChatNodes.ENTITY_EXTRACTOR, node_async(entityExtractorNode))
                .addNode(ChatNodes.SQL_GENERATOR, node_async(sqlGeneratorNode))
                .addNode(ChatNodes.SQL_EXECUTOR, node_async(sqlExecutorNode))
                .addNode(ChatNodes.FINAL_RESPONSE, node_async(finalResponseBuilderNode))
                .addEdge(START, ChatNodes.ENTITY_EXTRACTOR)
                .addEdge(ChatNodes.ENTITY_EXTRACTOR, ChatNodes.SQL_GENERATOR)
                .addEdge(ChatNodes.SQL_GENERATOR, ChatNodes.SQL_EXECUTOR)
                .addEdge(ChatNodes.SQL_EXECUTOR, ChatNodes.FINAL_RESPONSE)
                .addEdge(ChatNodes.FINAL_RESPONSE, END);

        this.compiledGraph = graph.compile(CompileConfig.builder()
                .checkpointSaver(checkpointSaver)
                .build());

        log.info("ChatGraph compiled: START -> {} -> {} -> {} -> {} -> END",
                ChatNodes.ENTITY_EXTRACTOR, ChatNodes.SQL_GENERATOR,
                ChatNodes.SQL_EXECUTOR, ChatNodes.FINAL_RESPONSE);
    }

    @Override
    public ChatState run(Map<String, Object> input, String threadId) {
        RunnableConfig config = RunnableConfig.builder().threadId(threadId).build();
        return compiledGraph.invoke(input, config)
                .orElseThrow(() -> new IllegalStateException("Chat graph returned no final state"));
    }
}