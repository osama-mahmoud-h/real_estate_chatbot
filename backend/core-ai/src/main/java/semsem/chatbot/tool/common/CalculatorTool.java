package semsem.chatbot.tool.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for mathematical calculations.
 */
@Slf4j
@Component
public class CalculatorTool extends BaseTool {

    public CalculatorTool() {
        this.name = "calculator";
        this.description = "Perform mathematical calculations";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "expression", Map.of("type", "string", "description", "Mathematical expression to evaluate")
                ),
                "required", new String[]{"expression"}
        );
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement safe expression evaluation
        String expression = (String) arguments.get("expression");
        return ToolResult.success("Calculation result for: " + expression);
    }
}
