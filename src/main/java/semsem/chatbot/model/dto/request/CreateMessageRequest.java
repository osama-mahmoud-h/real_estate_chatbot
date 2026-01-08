package semsem.chatbot.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatbot.model.enums.MessageRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new message")
public class CreateMessageRequest {

    @NotNull(message = "Role is required")
    @Schema(description = "Message role", example = "USER")
    private MessageRole role;

    @NotBlank(message = "Content is required")
    @Schema(description = "Message content", example = "What properties are available in downtown?")
    private String content;

    @Schema(description = "LLM provider used (for assistant messages)", example = "google")
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

    @Schema(description = "Message metadata as JSON string")
    private String metadata;
}
