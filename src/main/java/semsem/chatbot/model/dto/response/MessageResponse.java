package semsem.chatbot.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatbot.model.enums.MessageRole;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Message response object")
public class MessageResponse {

    @Schema(description = "Unique message identifier", example = "msg_456def")
    private String messageId;

    @Schema(description = "Conversation ID this message belongs to", example = "conv_123abc")
    private String conversationId;

    @Schema(description = "Message role")
    private MessageRole role;

    @Schema(description = "Message content")
    private String content;

    @Schema(description = "LLM provider used", example = "google")
    private String providerLlm;

    @Schema(description = "Model used for generation", example = "gemini-pro")
    private String modelUsed;

    @Schema(description = "Number of prompt tokens")
    private Integer promptTokens;

    @Schema(description = "Number of completion tokens")
    private Integer completionTokens;

    @Schema(description = "Total tokens used")
    private Integer totalTokens;

    @Schema(description = "Response latency in milliseconds")
    private Long latencyMs;

    @Schema(description = "Parent message ID for threading")
    private Long parentMessageId;

    @Schema(description = "Message metadata as JSON")
    private String metadata;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Processing completion timestamp")
    private Instant processedAt;
}
