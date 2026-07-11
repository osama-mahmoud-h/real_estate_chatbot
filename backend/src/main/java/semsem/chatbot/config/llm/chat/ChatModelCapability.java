package semsem.chatbot.config.llm.chat;

import semsem.chatbot.config.llm.ModelCapability;

/**
 * Enum for chat model capabilities.
 * Single responsibility: defines capabilities specific to chat/LLM models.
 */
public enum ChatModelCapability implements ModelCapability {

    STREAMING("streaming"),
    FUNCTION_CALLING("function-calling"),
    VISION("vision"),
    JSON_MODE("json-mode"),
    SYSTEM_PROMPT("system-prompt"),
    RAG("rag"),
    CODE_GENERATION("code-generation");

    private final String value;

    ChatModelCapability(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ChatModelCapability fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Capability value cannot be null");
        }
        for (ChatModelCapability cap : values()) {
            if (cap.value.equalsIgnoreCase(value)) {
                return cap;
            }
        }
        throw new IllegalArgumentException("Unknown chat capability: " + value);
    }

    public static ChatModelCapability fromValueOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (ChatModelCapability cap : values()) {
            if (cap.value.equalsIgnoreCase(value)) {
                return cap;
            }
        }
        return null;
    }
}