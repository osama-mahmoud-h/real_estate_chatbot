package semsem.chatbot.orchestration.common;

import java.util.Map;

public interface GraphOrchestrator<S> {
    S run(Map<String, Object> input, String threadId);
}