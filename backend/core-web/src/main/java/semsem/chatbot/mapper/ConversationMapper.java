package semsem.chatbot.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatbot.model.dto.request.CreateConversationRequest;
import semsem.chatbot.model.dto.request.UpdateConversationRequest;
import semsem.chatbot.model.dto.response.ConversationResponse;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.domain.user.AppUser;
import semsem.chatbot.domain.conversation.Conversation;
import semsem.chatbot.domain.conversation.Message;
import semsem.chatbot.domain.conversation.ConversationStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationMapper {

    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;

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
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .messageCount(messageCount)
                .lastMessage(lastMessageResponse)
                .build();
    }

    public Conversation toEntity(CreateConversationRequest request, AppUser user) {
        Instant now = Instant.now();

        return Conversation.builder()
                .title(request.getTitle() != null ? request.getTitle() : "New Conversation")
                .appUser(user)
                .status(ConversationStatus.ACTIVE)
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

        conversation.setUpdatedAt(Instant.now());
    }

}
