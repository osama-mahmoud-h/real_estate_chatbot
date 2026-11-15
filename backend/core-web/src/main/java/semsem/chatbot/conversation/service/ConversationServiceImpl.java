package semsem.chatbot.conversation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.common.exception.ApiException;
import semsem.chatbot.common.exception.ResourceNotFoundException;
import semsem.chatbot.conversation.mapper.ConversationMapper;
import semsem.chatbot.conversation.dto.CreateConversationRequest;
import semsem.chatbot.conversation.dto.UpdateConversationRequest;
import semsem.chatbot.conversation.dto.ConversationResponse;
import semsem.chatbot.user.domain.AppUser;
import semsem.chatbot.conversation.domain.Conversation;
import semsem.chatbot.conversation.domain.Message;
import semsem.chatbot.conversation.domain.ConversationStatus;
import semsem.chatbot.conversation.repository.ConversationRepository;
import semsem.chatbot.conversation.repository.MessageRepository;
import semsem.chatbot.user.repository.UserRepository;
import semsem.chatbot.conversation.service.ConversationService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    @Transactional
    @Override
    public ConversationResponse createConversation(String userEmail, CreateConversationRequest request) {
        log.debug("Creating conversation for userEmail={}, title='{}'",
                userEmail, request != null ? request.getTitle() : null);
        try {
            AppUser user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

            Conversation conversation = conversationMapper.toEntity(request, user);
            Conversation savedConversation = conversationRepository.save(conversation);

            log.info("Created conversation {} for user {}", savedConversation.getConversationId(), userEmail);

            return conversationMapper.toResponseWithMessageCount(savedConversation, 0);
        } catch (ResourceNotFoundException ex) {
            log.warn("Cannot create conversation: {}", ex.getMessage());
            throw ex;
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating conversation for user {}: {}", userEmail, ex.getMessage(), ex);
            throw new ApiException("Failed to create conversation: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ConversationResponse getConversation(String userEmail, Long conversationId, boolean includeMessages) {
        log.debug("Fetching conversation {} for userEmail={} (includeMessages={})",
                conversationId, userEmail, includeMessages);
        try {
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
        } catch (ResourceNotFoundException ex) {
            log.warn("Conversation lookup failed for user {}: {}", userEmail, ex.getMessage());
            throw ex;
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting conversation {} for user {}: {}", conversationId, userEmail, ex.getMessage(), ex);
            throw new ApiException("Failed to get conversation: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ConversationResponse> getUserConversations(String userEmail, Pageable pageable) {
        log.debug("Fetching conversations page for userEmail={} (page={}, size={})",
                userEmail, pageable != null ? pageable.getPageNumber() : 0, pageable != null ? pageable.getPageSize() : 0);
        try {
            Page<Conversation> conversations = conversationRepository.findByUserEmail(userEmail, pageable);
            return conversations.map(conv -> {
                int messageCount = (int) messageRepository.countByConversationId(conv.getConversationId());
                Message lastMessage = messageRepository.findLastMessageByConversationId(conv.getConversationId()).orElse(null);
                return conversationMapper.toResponseWithLastMessage(conv, lastMessage, messageCount);
            });
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting conversations for user {}: {}", userEmail, ex.getMessage(), ex);
            throw new ApiException("Failed to get user conversations: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ConversationResponse> getUserConversationsByStatus(
            Long userId,
            ConversationStatus status,
            Pageable pageable
    ) {
        try {
            Page<Conversation> conversations = conversationRepository.findByUserIdAndStatus(userId, status, pageable);
            return conversations.map(conv -> {
                int messageCount = (int) messageRepository.countByConversationId(conv.getConversationId());
                Message lastMessage = messageRepository.findLastMessageByConversationId(conv.getConversationId()).orElse(null);
                return conversationMapper.toResponseWithLastMessage(conv, lastMessage, messageCount);
            });
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting conversations by status {} for user {}: {}", status, userId, ex.getMessage(), ex);
            throw new ApiException("Failed to get conversations by status: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public ConversationResponse updateConversation(
            String userEmail,
            Long conversationId,
            UpdateConversationRequest request
    ) {
        try {
            Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

            conversationMapper.updateEntity(conversation, request);
            Conversation updated = conversationRepository.save(conversation);

            log.info("Updated conversation {}", conversationId);

            int messageCount = (int) messageRepository.countByConversationId(conversationId);
            return conversationMapper.toResponseWithMessageCount(updated, messageCount);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to update conversation: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void archiveConversation(String userEmail, Long conversationId) {
        try {
            Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

            conversation.archive();
            conversationRepository.save(conversation);

            log.info("Archived conversation {}", conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error archiving conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to archive conversation: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteConversation(String userEmail, Long conversationId) {
        try {
            Conversation conversation = conversationRepository.findByUserEmailAndConversationId(userEmail, conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

            // cascade + orphanRemoval take the messages with it
            conversationRepository.delete(conversation);

            log.info("Deleted conversation {}", conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to delete conversation: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ConversationResponse> searchConversations(String userEmail, String keyword) {
        try {
            AppUser user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

            List<Conversation> conversations = conversationRepository.searchByTitleAndUserId(keyword, user.getUserId());
            return conversations.stream()
                    .map(conv -> {
                        int messageCount = (int) messageRepository.countByConversationId(conv.getConversationId());
                        return conversationMapper.toResponseWithMessageCount(conv, messageCount);
                    })
                    .collect(Collectors.toList());
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error searching conversations for user {}: {}", userEmail, ex.getMessage(), ex);
            throw new ApiException("Failed to search conversations: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void updateConversationSummary(Long conversationId, String summary) {
        try {
            Conversation conversation = conversationRepository.findByConversationId(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));

            conversation.summarize(summary);
            conversationRepository.save(conversation);

            log.debug("Updated summary for conversation {}", conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating summary for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to update conversation summary: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public long getConversationCount(Long userId) {
        try {
            return conversationRepository.countByUserId(userId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting conversation count for user {}: {}", userId, ex.getMessage(), ex);
            throw new ApiException("Failed to get conversation count: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public long getConversationCountByStatus(Long userId, ConversationStatus status) {
        try {
            return conversationRepository.countByUserIdAndStatus(userId, status);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting conversation count by status for user {}: {}", userId, ex.getMessage(), ex);
            throw new ApiException("Failed to get conversation count by status: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Conversation getConversationEntity(Long conversationId) {
        try {
            return conversationRepository.findByConversationId(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation", "conversationId", conversationId));
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting conversation entity {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get conversation: " + ex.getMessage());
        }
    }
}