package semsem.chatbot.service.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.exception.ApiException;
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
import semsem.chatbot.service.chat.MessageService;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageResponse createMessage(Long conversationId, CreateMessageRequest request) {
        try {
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
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating message in conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to create message: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public MessageResponse getMessage(Long messageId) {
        try {
            Message message = messageRepository.findByMessageId(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message", "messageId", messageId));

            return messageMapper.toResponse(message);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting message {}: {}", messageId, ex.getMessage(), ex);
            throw new ApiException("Failed to get message: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponse> getConversationMessages(Long conversationId) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            List<Message> messages = messageRepository.findByConversationId(conversationId);
            return messageMapper.toResponseList(messages);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting messages for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get conversation messages: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MessageResponse> getConversationMessagesPaged(Long conversationId, Pageable pageable) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            Page<Message> messages = messageRepository.findByConversationIdPaged(conversationId, pageable);
            return messages.map(messageMapper::toResponse);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting paged messages for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get paged conversation messages: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponse> getConversationMessagesByRole(Long conversationId, MessageRole role) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            List<Message> messages = messageRepository.findByConversationIdAndRole(conversationId, role);
            return messageMapper.toResponseList(messages);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting messages by role {} for conversation {}: {}", role, conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get messages by role: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponse> getRecentMessages(Long conversationId, int limit) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            List<Message> messages = messageRepository.findRecentMessages(conversationId, PageRequest.of(0, limit));
            return messageMapper.toResponseList(messages);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting recent messages for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get recent messages: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public MessageResponse getLastMessage(Long conversationId) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            Message message = messageRepository.findLastMessageByConversationId(conversationId)
                    .orElse(null);

            return messageMapper.toResponse(message);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting last message for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get last message: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public MessageResponse updateMessage(Long messageId, CreateMessageRequest request) {
        try {
            Message message = messageRepository.findByMessageId(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message", "messageId", messageId));

            messageMapper.updateEntity(message, request);
            Message updated = messageRepository.save(message);

            log.info("Updated message {}", messageId);

            return messageMapper.toResponse(updated);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating message {}: {}", messageId, ex.getMessage(), ex);
            throw new ApiException("Failed to update message: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteMessage(Long messageId) {
        try {
            Message message = messageRepository.findByMessageId(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message", "messageId", messageId));

            messageRepository.delete(message);

            log.info("Deleted message {}", messageId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting message {}: {}", messageId, ex.getMessage(), ex);
            throw new ApiException("Failed to delete message: " + ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteConversationMessages(Long conversationId) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            int deleted = messageRepository.deleteByConversationId(conversationId);
            log.info("Deleted {} messages from conversation {}", deleted, conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting messages for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to delete conversation messages: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public long getMessageCount(Long conversationId) {
        try {
            return messageRepository.countByConversationId(conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting message count for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get message count: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public long getMessageCountByRole(Long conversationId, MessageRole role) {
        try {
            return messageRepository.countByConversationIdAndRole(conversationId, role);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting message count by role for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get message count by role: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public int getTotalTokens(Long conversationId) {
        try {
            return messageRepository.sumTotalTokensByConversationId(conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting total tokens for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get total tokens: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Double getAverageLatency(Long conversationId) {
        try {
            return messageRepository.avgLatencyByConversationId(conversationId);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting average latency for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get average latency: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponse> searchMessages(Long conversationId, String keyword) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            List<Message> messages = messageRepository.searchByContentInConversation(keyword, conversationId);
            return messageMapper.toResponseList(messages);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error searching messages in conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to search messages: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponse> getMessagesSince(Long conversationId, Instant since) {
        try {
            if (!conversationRepository.existsByConversationId(conversationId)) {
                throw new ResourceNotFoundException("Conversation", "conversationId", conversationId);
            }

            List<Message> messages = messageRepository.findMessagesSince(conversationId, since);
            return messageMapper.toResponseList(messages);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting messages since {} for conversation {}: {}", since, conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get messages since timestamp: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponse> getChildMessages(Long parentMessageId) {
        try {
            List<Message> messages = messageRepository.findByParentMessageId(parentMessageId);
            return messageMapper.toResponseList(messages);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting child messages for parent {}: {}", parentMessageId, ex.getMessage(), ex);
            throw new ApiException("Failed to get child messages: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public MessageStatsResponse getMessageStats(Long conversationId) {
        try {
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
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error getting message stats for conversation {}: {}", conversationId, ex.getMessage(), ex);
            throw new ApiException("Failed to get message stats: " + ex.getMessage());
        }
    }
}