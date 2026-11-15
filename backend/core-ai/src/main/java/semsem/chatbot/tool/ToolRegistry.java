package semsem.chatbot.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for managing available tools.
 */
@Slf4j
@Service
public class ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();

    public void register(Tool tool) {
        if (tool == null || tool.getName() == null || tool.getName().isBlank()) {
            log.warn("Attempted to register a null tool or tool with blank name");
            return;
        }
        try {
            tools.put(tool.getName(), tool);
            log.info("Registered tool: {}", tool.getName());
        } catch (Exception ex) {
            log.error("Failed to register tool '{}': {}", tool.getName(), ex.getMessage(), ex);
        }
    }

    public void register(List<Tool> toolList) {
        if (toolList == null || toolList.isEmpty()) {
            log.debug("No tools provided to batch register");
            return;
        }
        toolList.forEach(this::register);
    }

    public Optional<Tool> get(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Requested tool with null or blank name");
            return Optional.empty();
        }
        Tool tool = tools.get(name);
        if (tool == null) {
            log.debug("Tool '{}' not found in ToolRegistry", name);
        }
        return Optional.ofNullable(tool);
    }

    public List<Tool> getAll() {
        return List.copyOf(tools.values());
    }

    public boolean exists(String name) {
        return name != null && tools.containsKey(name);
    }

    public void unregister(String name) {
        if (name != null && tools.remove(name) != null) {
            log.info("Unregistered tool: {}", name);
        }
    }

    public Map<String, Object> getToolDefinitions() {
        try {
            Map<String, Object> definitions = new HashMap<>();
            tools.forEach((name, tool) -> definitions.put(name, Map.of(
                    "description", tool.getDescription(),
                    "parameters", tool.getParameters()
            )));
            log.debug("Built tool definitions for {} registered tool(s)", definitions.size());
            return definitions;
        } catch (Exception ex) {
            log.error("Failed to build tool definitions: {}", ex.getMessage(), ex);
            return Collections.emptyMap();
        }
    }
}
