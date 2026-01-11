package semsem.chatbot.orchestration.node;

import lombok.RequiredArgsConstructor;
import semsem.chatbot.orchestration.graph.GraphState;
import semsem.chatbot.rag.retriever.Retriever;

/**
 * Node that performs document retrieval for RAG.
 */
@RequiredArgsConstructor
public class RAGNode<S extends GraphState> implements GraphNode<S> {

    private final String name;
    private final Retriever retriever;
    private final int topK;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public S execute(S state) {
        // TODO: Implement retrieval
        return state;
    }
}
