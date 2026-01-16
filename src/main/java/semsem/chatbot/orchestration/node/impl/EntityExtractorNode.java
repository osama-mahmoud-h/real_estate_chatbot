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
public class EntityExtractorNode implements GraphNode<ChatGraphState> {

    @Override
    public GraphNodeNames getName() {
        return GraphNodeNames.ENTITY_EXTRACTOR;
    }

    @Override
    public ChatGraphState execute(ChatGraphState state) {
        // TODO: Implement entity extraction logic
        log.debug("Executing EntityExtractorNode");
        return state;
    }
}
