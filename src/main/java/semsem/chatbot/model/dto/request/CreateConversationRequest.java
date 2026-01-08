package semsem.chatbot.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new conversation")
public class CreateConversationRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "Optional title for the conversation", example = "Real Estate Inquiry")
    private String title;

    @Schema(description = "Optional metadata as JSON string")
    private String metadata;

    @Schema(description = "Initial message to start the conversation (optional)")
    private String initialMessage;
}
