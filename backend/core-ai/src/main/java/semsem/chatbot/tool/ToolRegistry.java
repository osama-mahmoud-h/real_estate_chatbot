package semsem.chatbot.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for managing available tools.
 */
@Slf4j
@Service
public class ToolRegistry {

    private final Map<String, Tool> tools = new HashMap<>();

    public void register(Tool tool) {
        tools.put(tool.getName(), tool);
        log.info("Registered tool: {}", tool.getName());
    }

    public void register(List<Tool> toolList) {
        toolList.forEach(this::register);
    }

    public Optional<Tool> get(String name) {
        return Optional.ofNullable(tools.get(name));
    }

    public List<Tool> getAll() {
        return List.copyOf(tools.values());
    }

    public boolean exists(String name) {
        return tools.containsKey(name);
    }

    public void unregister(String name) {
        tools.remove(name);
    }

    public Map<String, Object> getToolDefinitions() {
        Map<String, Object> definitions = new HashMap<>();
        tools.forEach((name, tool) -> {
            definitions.put(name, Map.of(
                    "description", tool.getDescription(),
                    "parameters", tool.getParameters()
            ));
        });
        return definitions;
    }
}
