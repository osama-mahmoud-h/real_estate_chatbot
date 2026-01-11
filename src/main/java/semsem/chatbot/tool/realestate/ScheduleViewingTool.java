package semsem.chatbot.tool.realestate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for scheduling property viewings.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleViewingTool extends BaseTool {

    public ScheduleViewingTool() {
        this.name = "schedule_viewing";
        this.description = "Schedule a viewing appointment for a specific property";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "propertyId", Map.of("type", "string", "description", "Property ID to schedule viewing for"),
                        "preferredDate", Map.of("type", "string", "description", "Preferred date (YYYY-MM-DD)"),
                        "preferredTime", Map.of("type", "string", "description", "Preferred time slot"),
                        "contactName", Map.of("type", "string", "description", "Contact person name"),
                        "contactPhone", Map.of("type", "string", "description", "Contact phone number")
                ),
                "required", new String[]{"propertyId", "preferredDate", "contactName", "contactPhone"}
        );
    }

    @Override
    public boolean requiresConfirmation() {
        return true;
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement viewing scheduling
        return ToolResult.success("Viewing scheduled successfully");
    }
}
