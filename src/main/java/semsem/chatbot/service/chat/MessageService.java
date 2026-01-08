package semsem.chatbot.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.exception.ResourceNotFoundException;
import semsem.chatbot.mapper.MessageMapper;
import semsem.chatbot.model.dto.request.CreateMessageRequest;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.model.dto.response.MessageStatsResponse;
import semsem.chatbot.model.entity.Conversation;
import semsem.chatbot.model.entity.Message;
import semsem.chatbot.model.enums.MessageRole;
import semsem.chatbot.repository.ConversationRepository;
import semsem.chatbot.repository.MessageRepository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageResponse createMessage(String conversationId, CreateMessageRequest request) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

        Message message = messageMapper.toEntity(request, conversation);
        Message savedMessage = messageRepository.save(message);

        // Update conversation's updatedAt and token count
        conversation.setUpdatedAt(Instant.now());
        if (request.getTotalTokens() != null) {
            int currentTokens = conversation.getTokenCount() != null ? conversation.getTokenCount() : 0;
            conversation.setTokenCount(currentTokens + request.getTotalTokens());
        }
        conversationRepository.save(conversation);

        log.info("Created message {} in conversation {}", savedMessage.getMessageId(), conversationId);

        return messageMapper.toResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public MessageResponse getMessage(String messageId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "messageId", messageId));

        return messageMapper.toResponse(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversationMessages(String conversationId) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        List<Message> messages = messageRepository.findByConversationId(conversationId);
        return messageMapper.toResponseList(messages);
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getConversationMessagesPaged(String conversationId, Pageable pageable) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        Page<Message> messages = messageRepository.findByConversationIdPaged(conversationId, pageable);
        return messages.map(messageMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getConversationMessagesByRole(String conversationId, MessageRole role) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        List<Message> messages = messageRepository.findByConversationIdAndRole(conversationId, role);
        return messageMapper.toResponseList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getRecentMessages(String conversationId, int limit) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        List<Message> messages = messageRepository.findRecentMessages(conversationId, PageRequest.of(0, limit));
        return messageMapper.toResponseList(messages);
    }

    @Transactional(readOnly = true)
    public MessageResponse getLastMessage(String conversationId) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        Message message = messageRepository.findLastMessageByConversationId(conversationId)
                .orElse(null);

        return messageMapper.toResponse(message);
    }

    @Transactional
    public MessageResponse updateMessage(String messageId, CreateMessageRequest request) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "messageId", messageId));

        messageMapper.updateEntity(message, request);
        Message updated = messageRepository.save(message);

        log.info("Updated message {}", messageId);

        return messageMapper.toResponse(updated);
    }

    @Transactional
    public void deleteMessage(String messageId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "messageId", messageId));

        messageRepository.delete(message);

        log.info("Deleted message {}", messageId);
    }

    @Transactional
    public void deleteConversationMessages(String conversationId) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        int deleted = messageRepository.deleteByConversationId(conversationId);
        log.info("Deleted {} messages from conversation {}", deleted, conversationId);
    }

    @Transactional(readOnly = true)
    public long getMessageCount(String conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public long getMessageCountByRole(String conversationId, MessageRole role) {
        return messageRepository.countByConversationIdAndRole(conversationId, role);
    }

    @Transactional(readOnly = true)
    public int getTotalTokens(String conversationId) {
        return messageRepository.sumTotalTokensByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public Double getAverageLatency(String conversationId) {
        return messageRepository.avgLatencyByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> searchMessages(String conversationId, String keyword) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        List<Message> messages = messageRepository.searchByContentInConversation(keyword, conversationId);
        return messageMapper.toResponseList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessagesSince(String conversationId, Instant since) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        List<Message> messages = messageRepository.findMessagesSince(conversationId, since);
        return messageMapper.toResponseList(messages);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getChildMessages(Long parentMessageId) {
        List<Message> messages = messageRepository.findByParentMessageId(parentMessageId);
        return messageMapper.toResponseList(messages);
    }

    @Transactional(readOnly = true)
    public MessageStatsResponse getMessageStats(String conversationId) {
        if (!conversationRepository.existsByConversationId(conversationId)) {
            throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
        }

        long totalCount = messageRepository.countByConversationId(conversationId);
        long userCount = messageRepository.countByConversationIdAndRole(conversationId, MessageRole.USER);
        long assistantCount = messageRepository.countByConversationIdAndRole(conversationId, MessageRole.ASSISTANT);
        int totalTokens = messageRepository.sumTotalTokensByConversationId(conversationId);
        Double avgLatency = messageRepository.avgLatencyByConversationId(conversationId);

        return MessageStatsResponse.builder()
                .totalMessages(totalCount)
                .userMessages(userCount)
                .assistantMessages(assistantCount)
                .totalTokens(totalTokens)
                .averageLatencyMs(avgLatency != null ? avgLatency : 0)
                .build();
    }
}
