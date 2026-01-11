package semsem.chatbot.tool.realestate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for searching real estate properties.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PropertySearchTool extends BaseTool {

    public PropertySearchTool() {
        this.name = "property_search";
        this.description = "Search for real estate properties based on criteria like location, price range, property type, bedrooms, etc.";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "location", Map.of("type", "string", "description", "City or area to search"),
                        "minPrice", Map.of("type", "number", "description", "Minimum price"),
                        "maxPrice", Map.of("type", "number", "description", "Maximum price"),
                        "propertyType", Map.of("type", "string", "description", "Type: apartment, house, villa, land"),
                        "bedrooms", Map.of("type", "integer", "description", "Number of bedrooms")
                ),
                "required", new String[]{"location"}
        );
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement property search
        return ToolResult.success("Found properties matching criteria");
    }
}
