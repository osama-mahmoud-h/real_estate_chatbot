package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.rag.retriever.Retriever;

/**
 * Node that performs document retrieval for RAG.
 */
@RequiredArgsConstructor
public class RAGNode<S extends GraphState> implements GraphNode<S> {

    private final GraphNodeNames name;
    private final Retriever retriever;
    private final int topK;

    @Override
    public GraphNodeNames getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement retrieval
        return state;
    }
}
