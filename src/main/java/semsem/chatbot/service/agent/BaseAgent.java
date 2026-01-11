package semsem.chatbot.service.agent;

import lombok.Getter;
import lombok.Setter;
import semsem.chatbot.service.llm.LLMService;
import semsem.chatbot.tool.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for agents with common functionality.
 */
@Getter
@Setter
public abstract class BaseAgent implements Agent {

    protected String name;
    protected LLMService llmService;
    protected List<Tool> tools = new ArrayList<>();
    protected int maxIterations = 10;
    protected String systemPrompt;

    @Override
    public List<Tool> getTools() {
        return tools;
    }

    @Override
    public void addTool(Tool tool) {
        tools.add(tool);
    }

    @Override
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public AgentResponse run(String input) {
        return run(input, Map.of());
    }

    protected abstract AgentResponse executeLoop(String input, Map<String, Object> context);

    @Override
    public AgentResponse run(String input, Map<String, Object> context) {
        long startTime = System.currentTimeMillis();
        try {
            AgentResponse response = executeLoop(input, context);
            response.setLatencyMs(System.currentTimeMillis() - startTime);
            return response;
        } catch (Exception e) {
            return AgentResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .latencyMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}
