package semsem.chatbot.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatbot.model.dto.request.CreateConversationRequest;
import semsem.chatbot.model.dto.request.UpdateConversationRequest;
import semsem.chatbot.model.dto.response.ConversationResponse;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.model.entity.AppUser;
import semsem.chatbot.model.entity.Conversation;
import semsem.chatbot.model.entity.Message;
import semsem.chatbot.model.enums.ConversationStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationMapper {

    private final MessageMapper messageMapper;

    public ConversationResponse toResponse(Conversation conversation) {
        if (conversation == null) {
            return null;
        }

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .title(conversation.getTitle())
                .status(conversation.getStatus())
                .summary(conversation.getSummary())
                .tokenCount(conversation.getTokenCount())
                .metadata(conversation.getMetadata())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public ConversationResponse toResponseWithDetails(
            Conversation conversation,
            List<Message> messages,
            Message lastMessage,
            int messageCount
    ) {
        if (conversation == null) {
            return null;
        }

        List<MessageResponse> messageResponses = messages != null ?
                messageMapper.toResponseList(messages) : null;

        MessageResponse lastMessageResponse = lastMessage != null ?
                messageMapper.toResponse(lastMessage) : null;

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .title(conversation.getTitle())
                .status(conversation.getStatus())
                .summary(conversation.getSummary())
                .tokenCount(conversation.getTokenCount())
                .metadata(conversation.getMetadata())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .messageCount(messageCount)
                .messages(messageResponses)
                .lastMessage(lastMessageResponse)
                .build();
    }

    public ConversationResponse toResponseWithMessageCount(Conversation conversation, int messageCount) {
        if (conversation == null) {
            return null;
        }

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .title(conversation.getTitle())
                .status(conversation.getStatus())
                .summary(conversation.getSummary())
                .tokenCount(conversation.getTokenCount())
                .metadata(conversation.getMetadata())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .messageCount(messageCount)
                .build();
    }

    public ConversationResponse toResponseWithLastMessage(
            Conversation conversation,
            Message lastMessage,
            int messageCount
    ) {
        if (conversation == null) {
            return null;
        }

        MessageResponse lastMessageResponse = lastMessage != null ?
                messageMapper.toResponse(lastMessage) : null;

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .title(conversation.getTitle())
                .status(conversation.getStatus())
                .summary(conversation.getSummary())
                .tokenCount(conversation.getTokenCount())
                .metadata(conversation.getMetadata())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .messageCount(messageCount)
                .lastMessage(lastMessageResponse)
                .build();
    }

    public Conversation toEntity(CreateConversationRequest request, AppUser user) {
        String conversationId = generateConversationId();
        Instant now = Instant.now();

        return Conversation.builder()
                .conversationId(conversationId)
                .title(request.getTitle() != null ? request.getTitle() : "New Conversation")
                .appUser(user)
                .status(ConversationStatus.ACTIVE)
                .metadata(request.getMetadata())
                .tokenCount(0)
                .messages(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateEntity(Conversation conversation, UpdateConversationRequest request) {
        if (request.getTitle() != null) {
            conversation.setTitle(request.getTitle());
        }
        if (request.getStatus() != null) {
            conversation.setStatus(request.getStatus());
        }
        if (request.getMetadata() != null) {
            conversation.setMetadata(request.getMetadata());
        }
        conversation.setUpdatedAt(Instant.now());
    }

    private String generateConversationId() {
        return "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
