package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.tool.Tool;

import java.util.List;

/**
 * Node that executes tools/functions.
 */
@RequiredArgsConstructor
public class ToolNode<S extends GraphState> implements GraphNode<S> {

    private final GraphNodeNames name;
    private final List<Tool> tools;

    @Override
    public GraphNodeNames getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement tool execution
        return state;
    }
}
