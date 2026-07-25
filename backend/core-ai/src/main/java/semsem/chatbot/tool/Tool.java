package semsem.chatbot.tool;

import java.util.Map;

/**
 * Interface for agent tools/functions.
 */
public interface Tool {

    String getName();

    String getDescription();

    Map<String, Object> getParameters();

    ToolResult execute(Map<String, Object> arguments);

    default boolean requiresConfirmation() {
        return false;
    }
}
