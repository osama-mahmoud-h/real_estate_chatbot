package semsem.chatbot.mapper;

import org.springframework.stereotype.Service;
import semsem.chatbot.model.dto.request.CreateMessageRequest;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.model.entity.Conversation;
import semsem.chatbot.model.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
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
                .promptTokens(message.getPromptTokens())
                .completionTokens(message.getCompletionTokens())
                .totalTokens(message.getTotalTokens())
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

    public Message toEntity(CreateMessageRequest request, Conversation conversation) {
        String messageId = generateMessageId();

        return Message.builder()
                .messageId(messageId)
                .conversation(conversation)
                .role(request.getRole())
                .content(request.getContent())
                .providerLlm(request.getProviderLlm())
                .modelUsed(request.getModelUsed())
                .promptTokens(request.getPromptTokens())
                .completionTokens(request.getCompletionTokens())
                .totalTokens(request.getTotalTokens())
                .latencyMs(request.getLatencyMs())
                .parentMessageId(request.getParentMessageId())
                .metadata(request.getMetadata())
                .processedAt(request.getLatencyMs() != null ? Instant.now() : null)
                .build();
    }

    public void updateEntity(Message message, CreateMessageRequest request) {
        if (request.getContent() != null) {
            message.setContent(request.getContent());
        }
        if (request.getMetadata() != null) {
            message.setMetadata(request.getMetadata());
        }
        if (request.getProviderLlm() != null) {
            message.setProviderLlm(request.getProviderLlm());
        }
        if (request.getModelUsed() != null) {
            message.setModelUsed(request.getModelUsed());
        }
        if (request.getPromptTokens() != null) {
            message.setPromptTokens(request.getPromptTokens());
        }
        if (request.getCompletionTokens() != null) {
            message.setCompletionTokens(request.getCompletionTokens());
        }
        if (request.getTotalTokens() != null) {
            message.setTotalTokens(request.getTotalTokens());
        }
        if (request.getLatencyMs() != null) {
            message.setLatencyMs(request.getLatencyMs());
            message.setProcessedAt(Instant.now());
        }
    }

    private String generateMessageId() {
        return "msg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
