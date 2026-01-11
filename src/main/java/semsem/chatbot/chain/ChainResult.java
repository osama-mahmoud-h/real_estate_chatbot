package semsem.chatbot.chain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Result container for chain execution.
 */
@Data
@Builder
public class ChainResult {

    private String output;
    private Map<String, Object> metadata;
    private int tokensUsed;
    private long latencyMs;
    private boolean success;
    private String errorMessage;

    public static ChainResult success(String output) {
        return ChainResult.builder()
                .output(output)
                .success(true)
                .build();
    }

    public static ChainResult failure(String errorMessage) {
        return ChainResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
