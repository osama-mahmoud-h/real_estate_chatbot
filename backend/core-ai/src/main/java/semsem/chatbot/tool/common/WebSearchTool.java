package semsem.chatbot.tool.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import semsem.chatbot.tool.BaseTool;
import semsem.chatbot.tool.ToolResult;

import java.util.Map;

/**
 * Tool for web search functionality.
 */
@Slf4j
@Component
public class WebSearchTool extends BaseTool {

    public WebSearchTool() {
        this.name = "web_search";
        this.description = "Search the web for information";
        this.parameters = Map.of(
                "type", "object",
                "properties", Map.of(
                        "query", Map.of("type", "string", "description", "Search query"),
                        "numResults", Map.of("type", "integer", "description", "Number of results to return")
                ),
                "required", new String[]{"query"}
        );
    }

    @Override
    protected ToolResult doExecute(Map<String, Object> arguments) {
        // TODO: Implement web search
        return ToolResult.success("Search results");
    }
}
