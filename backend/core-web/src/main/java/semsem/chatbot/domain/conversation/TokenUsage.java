package semsem.chatbot.domain.conversation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * What one model call cost. Historical rows predate token accounting, so every
 * component is nullable and {@link #total()} answers 0 rather than null.
 */
@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenUsage {

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    public static TokenUsage none() {
        return new TokenUsage(null, null, null);
    }

    public static TokenUsage of(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        if (promptTokens == null && completionTokens == null && totalTokens == null) {
            return none();
        }
        return new TokenUsage(promptTokens, completionTokens, totalTokens);
    }

    public int total() {
        return totalTokens != null ? totalTokens : 0;
    }

    public boolean isRecorded() {
        return promptTokens != null || completionTokens != null || totalTokens != null;
    }
}