package semsem.chatbot.orchestration.workflow;

import java.util.Map;

public interface GraphOrchestrator<S> {
    S run(Map<String, Object> input, String threadId);
}