package semsem.chatbot.orchestration.checkpointer;

import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of Checkpointer.
 * Suitable for development and single-instance deployments.
 */
@Component
public class InMemoryCheckpointer implements Checkpointer {

    private final ConcurrentHashMap<String, GraphState> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String threadId, String nodeId, GraphState state) {
        // TODO: Implement
    }

    @Override
    public Optional<GraphState> load(String threadId, String nodeId) {
        // TODO: Implement
        return Optional.empty();
    }

    @Override
    public Optional<GraphState> loadLatest(String threadId) {
        // TODO: Implement
        return Optional.empty();
    }

    @Override
    public void delete(String threadId) {
        // TODO: Implement
    }

    @Override
    public boolean exists(String threadId) {
        // TODO: Implement
        return false;
    }
}
