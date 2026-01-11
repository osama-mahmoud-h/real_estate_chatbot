package semsem.chatbot.service.agent;

import semsem.chatbot.tool.Tool;

import java.util.List;
import java.util.Map;

/**
 * Base interface for AI agents.
 * Agents are autonomous entities that can reason and take actions.
 */
public interface Agent {

    String getName();

    AgentResponse run(String input);

    AgentResponse run(String input, Map<String, Object> context);

    List<Tool> getTools();

    void addTool(Tool tool);

    void setMaxIterations(int maxIterations);
}
