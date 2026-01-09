package semsem.chatbot.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import semsem.chatbot.model.enums.ConversationStatus;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Conversation response object")
public class ConversationResponse {

    @Schema(description = "Unique conversation identifier", example = "conv_123abc")
    private String conversationId;

    @Schema(description = "Conversation title", example = "Real Estate Inquiry")
    private String title;

    @Schema(description = "Conversation status")
    private ConversationStatus status;

    @Schema(description = "Conversation summary")
    private String summary;

    @Schema(description = "Total token count in conversation")
    private Integer tokenCount;

    @Schema(description = "Number of messages in conversation")
    private Integer messageCount;

    @Schema(description = "Conversation metadata as JSON")
    private String metadata;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @Schema(description = "Messages in the conversation (optional)")
    private List<MessageResponse> messages;

    @Schema(description = "Last message preview")
    private MessageResponse lastMessage;
}
