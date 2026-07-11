package semsem.chatbot.exception;

/**
 * Exception thrown when prompt loading fails.
 */
public class PromptLoadException extends RuntimeException {

    public PromptLoadException(String message) {
        super(message);
    }

    public PromptLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
