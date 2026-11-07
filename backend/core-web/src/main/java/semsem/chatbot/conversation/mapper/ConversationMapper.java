package semsem.chatbot.conversation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatbot.conversation.dto.CreateConversationRequest;
import semsem.chatbot.conversation.dto.UpdateConversationRequest;
import semsem.chatbot.conversation.dto.ConversationResponse;
import semsem.chatbot.conversation.dto.MessageResponse;
import semsem.chatbot.user.domain.AppUser;
import semsem.chatbot.conversation.domain.Conversation;
import semsem.chatbot.conversation.domain.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

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
        return Conversation.start(user, request.getTitle());
    }

    public void updateEntity(Conversation conversation, UpdateConversationRequest request) {
        if (request.getTitle() != null) {
            conversation.rename(request.getTitle());
        }
        if (request.getStatus() != null) {
            conversation.changeStatus(request.getStatus());
        }
    }

}
