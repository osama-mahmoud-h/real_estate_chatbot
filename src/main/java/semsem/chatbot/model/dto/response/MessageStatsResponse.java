package semsem.chatbot.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message statistics for a conversation")
public class MessageStatsResponse {

    @Schema(description = "Total number of messages", example = "42")
    private long totalMessages;

    @Schema(description = "Number of user messages", example = "21")
    private long userMessages;

    @Schema(description = "Number of assistant messages", example = "21")
    private long assistantMessages;

    @Schema(description = "Total tokens used", example = "15000")
    private int totalTokens;

    @Schema(description = "Average response latency in milliseconds", example = "250.5")
    private double averageLatencyMs;
}
