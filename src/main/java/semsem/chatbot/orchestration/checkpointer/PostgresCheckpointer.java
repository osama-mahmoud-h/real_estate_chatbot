package semsem.chatbot.orchestration.checkpointer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Optional;

/**
 * PostgreSQL-backed implementation of Checkpointer.
 * Provides durable state persistence for production use.
 */
@Component
@RequiredArgsConstructor
public class PostgresCheckpointer implements Checkpointer {

    // TODO: Inject repository

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
