package semsem.chatbot.tool.realestate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for getting neighborhood information.
 */
@Slf4j
@Component
public class NeighborhoodInfoTool extends BaseTool {

    public NeighborhoodInfoTool() {
        this.name = "neighborhood_info";
        this.description = "Get information about a neighborhood including schools, amenities, safety, and demographics";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "location", Map.of("type", "string", "description", "Neighborhood or area name"),
                        "infoType", Map.of("type", "string", "description", "Type of info: schools, safety, amenities, demographics, all")
                ),
                "required", new String[]{"location"}
        );
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement neighborhood info fetching
        return ToolResult.success("Neighborhood information retrieved");
    }
}
