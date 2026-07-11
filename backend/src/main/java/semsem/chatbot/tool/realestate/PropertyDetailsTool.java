package semsem.chatbot.tool.realestate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for fetching detailed property information.
 */
@Slf4j
@Component
public class PropertyDetailsTool extends BaseTool {

    public PropertyDetailsTool() {
        this.name = "property_details";
        this.description = "Get detailed information about a specific property including features, amenities, and history";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "propertyId", Map.of("type", "string", "description", "Property ID to get details for")
                ),
                "required", new String[]{"propertyId"}
        );
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement property details fetching
        return ToolResult.success("Property details retrieved");
    }
}
