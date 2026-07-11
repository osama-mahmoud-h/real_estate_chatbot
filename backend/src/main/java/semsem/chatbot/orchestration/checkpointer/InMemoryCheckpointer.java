package semsem.chatbot.orchestration.checkpointer;

import org.springframework.stereotype.Component;
import semsem.chatbot.model.enums.GraphNodeNames;
import semsem.chatbot.orchestration.graph.GraphState;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of Checkpointer.
 * Suitable for development and single-instance deployments.
 */
@Component
public class InMemoryCheckpointer implements Checkpointer {

    // Key: threadId -> (nodeId -> state)
    private final Map<String, Map<GraphNodeNames, GraphState>> storage = new ConcurrentHashMap<>();
    // Track latest node per thread
    private final Map<String, GraphNodeNames> latestNode = new ConcurrentHashMap<>();

    @Override
    public void save(String threadId, GraphNodeNames nodeId, GraphState state) {
        storage.computeIfAbsent(threadId, k -> new ConcurrentHashMap<>())
                .put(nodeId, state);
        latestNode.put(threadId, nodeId);
    }

    @Override
    public Optional<GraphState> load(String threadId, GraphNodeNames nodeId) {
        Map<GraphNodeNames, GraphState> threadStates = storage.get(threadId);
        if (threadStates == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(threadStates.get(nodeId));
    }

    @Override
    public Optional<GraphState> loadLatest(String threadId) {
        GraphNodeNames latest = latestNode.get(threadId);
        if (latest == null) {
            return Optional.empty();
        }
        return load(threadId, latest);
    }

    @Override
    public void delete(String threadId) {
        storage.remove(threadId);
        latestNode.remove(threadId);
    }

    @Override
    public boolean exists(String threadId) {
        return storage.containsKey(threadId);
    }
}
