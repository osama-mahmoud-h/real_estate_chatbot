package semsem.chatbot.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatbot.model.enums.ConversationStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a conversation")
public class UpdateConversationRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Schema(description = "New title for the conversation", example = "Updated Title")
    private String title;

    @Schema(description = "New status for the conversation")
    private ConversationStatus status;

    @Schema(description = "Updated metadata as JSON string")
    private String metadata;
}
