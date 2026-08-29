package semsem.chatbot.mapper;

import org.springframework.stereotype.Service;
import semsem.chatbot.model.dto.request.CreateMessageRequest;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.domain.conversation.Conversation;
import semsem.chatbot.domain.conversation.Message;
import semsem.chatbot.domain.conversation.TokenUsage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageMapper {

    public MessageResponse toResponse(Message message) {
        if (message == null) {
            return null;
        }

        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .conversationId(message.getConversation() != null ?
                        message.getConversation().getConversationId() : null)
                .role(message.getRole())
                .content(message.getContent())
                .providerLlm(message.getProviderLlm())
                .modelUsed(message.getModelUsed())
                .promptTokens(message.getTokenUsage().getPromptTokens())
                .completionTokens(message.getTokenUsage().getCompletionTokens())
                .totalTokens(message.getTokenUsage().getTotalTokens())
                .latencyMs(message.getLatencyMs())
                .parentMessageId(message.getParentMessageId())
                .metadata(message.getMetadata())
                .createdAt(message.getCreatedAt())
                .processedAt(message.getProcessedAt())
                .build();
    }

    public List<MessageResponse> toResponseList(List<Message> messages) {
        if (messages == null) {
            return List.of();
        }
        return messages.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Builds a detached message. The caller attaches it via
     * {@link Conversation#addMessage}, which owns the conversation's counters.
     */
    public Message toEntity(CreateMessageRequest request) {
        Message message = Message.from(
                request.getRole(),
                request.getContent(),
                request.getParentMessageId(),
                request.getMetadata()
        );
        message.recordGeneration(
                request.getProviderLlm(),
                request.getModelUsed(),
                usageOf(request),
                request.getLatencyMs()
        );
        return message;
    }

    public void updateEntity(Message message, CreateMessageRequest request) {
        if (request.getContent() != null) {
            message.editContent(request.getContent());
        }
        if (request.getMetadata() != null) {
            message.describeWith(request.getMetadata());
        }
        message.recordGeneration(
                request.getProviderLlm() != null ? request.getProviderLlm() : message.getProviderLlm(),
                request.getModelUsed() != null ? request.getModelUsed() : message.getModelUsed(),
                mergeUsage(message.getTokenUsage(), request),
                request.getLatencyMs() != null ? request.getLatencyMs() : message.getLatencyMs()
        );
    }

    private TokenUsage usageOf(CreateMessageRequest request) {
        return TokenUsage.of(
                request.getPromptTokens(),
                request.getCompletionTokens(),
                request.getTotalTokens()
        );
    }

    private TokenUsage mergeUsage(TokenUsage current, CreateMessageRequest request) {
        return TokenUsage.of(
                request.getPromptTokens() != null ? request.getPromptTokens() : current.getPromptTokens(),
                request.getCompletionTokens() != null ? request.getCompletionTokens() : current.getCompletionTokens(),
                request.getTotalTokens() != null ? request.getTotalTokens() : current.getTotalTokens()
        );
    }


}
