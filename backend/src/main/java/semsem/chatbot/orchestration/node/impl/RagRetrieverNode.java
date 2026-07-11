package semsem.chatbot.orchestration.node.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.ChatGraphState;
import semsem.chatbot.orchestration.node.GraphNode;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagRetrieverNode implements GraphNode<ChatGraphState> {

    @Override
    public GraphNodeNames getName() {
        return GraphNodeNames.RAG_RETRIEVER;
    }

    @Override
    public ChatGraphState execute(ChatGraphState state) {
        // TODO: Implement RAG retrieval logic
        log.debug("Executing RagRetrieverNode");
        return state;
    }
}
