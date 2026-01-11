package semsem.chatbot.tool;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Abstract base class for tools.
 */
@Slf4j
@Getter
@Setter
public abstract class BaseTool implements Tool {

    protected String name;
    protected String description;
    protected Map<String, Object> parameters;

    @Override
    public ToolResult execute(Map<String, Object> arguments) {
        long startTime = System.currentTimeMillis();
        try {
            ToolResult result = doExecute(arguments);
            result.setToolName(getName());
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            log.error("Tool {} execution failed", getName(), e);
            return ToolResult.builder()
                    .toolName(getName())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    protected abstract ToolResult doExecute(Map<String, Object> arguments);
}
