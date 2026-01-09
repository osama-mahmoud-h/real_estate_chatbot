package semsem.chatbot.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.exception.ResourceNotFoundException;
import semsem.chatbot.mapper.ConversationMapper;
import semsem.chatbot.model.dto.request.CreateConversationRequest;
import semsem.chatbot.model.dto.request.UpdateConversationRequest;
import semsem.chatbot.model.dto.response.ConversationResponse;
import semsem.chatbot.model.entity.AppUser;
import semsem.chatbot.model.entity.Conversation;
import semsem.chatbot.model.entity.Message;
import semsem.chatbot.model.enums.ConversationStatus;
import semsem.chatbot.repository.ConversationRepository;
import semsem.chatbot.repository.MessageRepository;
import semsem.chatbot.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    @Transactional
    public ConversationResponse createConversation(String userEmail, CreateConversationRequest request) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Conversation conversation = conversationMapper.toEntity(request, user);
        Conversation savedConversation = conversationRepository.save(conversation);

        log.info("Created conversation {} for user {}", savedConversation.getConversationId(), userEmail);

        return conversationMapper.toResponseWithMessageCount(savedConversation, 0);
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversation(String userEmail, String conversationId, boolean includeMessages) {
        Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

        int messageCount = (int) messageRepository.countByConversationId(conversationId);

        if (includeMessages) {
            List<Message> messages = messageRepository.findByConversationId(conversationId);
            Message lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1);
            return conversationMapper.toResponseWithDetails(conversation, messages, lastMessage, messageCount);
        }

        Message lastMessage = messageRepository.findLastMessageByConversationId(conversationId).orElse(null);
        return conversationMapper.toResponseWithLastMessage(conversation, lastMessage, messageCount);
    }

    @Transactional(readOnly = true)
    public Page<ConversationResponse> getUserConversations(String userEmail, Pageable pageable) {
        Page<Conversation> conversations = conversationRepository.findByUserEmail(userEmail, pageable);
        return conversations.map(conv -> {
            int messageCount = (int) messageRepository.countByConversationId(conv.getConversationId());
            Message lastMessage = messageRepository.findLastMessageByConversationId(conv.getConversationId()).orElse(null);
            return conversationMapper.toResponseWithLastMessage(conv, lastMessage, messageCount);
        });
    }

    @Transactional(readOnly = true)
    public Page<ConversationResponse> getUserConversationsByStatus(
            Long userId,
            ConversationStatus status,
            Pageable pageable
    ) {
        Page<Conversation> conversations = conversationRepository.findByUserIdAndStatus(userId, status, pageable);
        return conversations.map(conv -> {
            int messageCount = (int) messageRepository.countByConversationId(conv.getConversationId());
            Message lastMessage = messageRepository.findLastMessageByConversationId(conv.getConversationId()).orElse(null);
            return conversationMapper.toResponseWithLastMessage(conv, lastMessage, messageCount);
        });
    }

    @Transactional
    public ConversationResponse updateConversation(
            String userEmail,
            String conversationId,
            UpdateConversationRequest request
    ) {
        Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

        conversationMapper.updateEntity(conversation, request);
        Conversation updated = conversationRepository.save(conversation);

        log.info("Updated conversation {}", conversationId);

        int messageCount = (int) messageRepository.countByConversationId(conversationId);
        return conversationMapper.toResponseWithMessageCount(updated, messageCount);
    }

    @Transactional
    public void archiveConversation(String userEmail, String conversationId) {
        Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

        conversation.setStatus(ConversationStatus.ARCHIVED);
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        log.info("Archived conversation {}", conversationId);
    }

    @Transactional
    public void deleteConversation(String userEmail, String conversationId) {
        Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

        messageRepository.deleteByConversationId(conversationId);
        conversationRepository.delete(conversation);

        log.info("Deleted conversation {}", conversationId);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> searchConversations(String userEmail, String keyword) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        List<Conversation> conversations = conversationRepository.searchByTitleAndUserId(keyword, user.getUserId());
        return conversations.stream()
                .map(conv -> {
                    int messageCount = (int) messageRepository.countByConversationId(conv.getConversationId());
                    return conversationMapper.toResponseWithMessageCount(conv, messageCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateConversationSummary(String conversationId, String summary) {
        conversationRepository.updateSummary(conversationId, summary, Instant.now());
        log.debug("Updated summary for conversation {}", conversationId);
    }

    @Transactional
    public void updateTokenCount(String conversationId, int additionalTokens) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

        int newCount = (conversation.getTokenCount() != null ? conversation.getTokenCount() : 0) + additionalTokens;
        conversation.setTokenCount(newCount);
        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public long getConversationCount(Long userId) {
        return conversationRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getConversationCountByStatus(Long userId, ConversationStatus status) {
        return conversationRepository.countByUserIdAndStatus(userId, status);
    }

    @Transactional(readOnly = true)
    public Conversation getConversationEntity(String conversationId) {
        return conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));
    }
}
