package semsem.chatbot.orchestration.checkpointer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Optional;

/**
 * PostgreSQL-backed implementation of Checkpointer.
 * Provides durable state persistence for production use.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostgresCheckpointer implements Checkpointer {

    // TODO: Inject CheckpointRepository when entity is created

    @Override
    public void save(String threadId, GraphNodeNames nodeId, GraphState state) {
        // TODO: Implement - serialize state to JSON and save to DB
        log.debug("Saving checkpoint for thread {} at node {}", threadId, nodeId);
    }

    @Override
    public Optional<GraphState> load(String threadId, GraphNodeNames nodeId) {
        // TODO: Implement - load from DB and deserialize
        log.debug("Loading checkpoint for thread {} at node {}", threadId, nodeId);
        return Optional.empty();
    }

    @Override
    public Optional<GraphState> loadLatest(String threadId) {
        // TODO: Implement - find latest checkpoint by timestamp
        log.debug("Loading latest checkpoint for thread {}", threadId);
        return Optional.empty();
    }

    @Override
    public void delete(String threadId) {
        // TODO: Implement - delete all checkpoints for thread
        log.debug("Deleting checkpoints for thread {}", threadId);
    }

    @Override
    public boolean exists(String threadId) {
        // TODO: Implement - check if any checkpoint exists
        return false;
    }
}
