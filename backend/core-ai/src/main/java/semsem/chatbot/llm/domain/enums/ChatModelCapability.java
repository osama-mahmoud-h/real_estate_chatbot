package semsem.chatbot.llm.domain.enums;

/**
 * Capabilities specific to chat/generative LLM models.
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

    @Override
    public String getValue() {
        return value;
    }
}
