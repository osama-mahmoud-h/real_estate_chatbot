package semsem.chatbot.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.model.dto.request.CreateConversationRequest;
import semsem.chatbot.model.dto.request.UpdateConversationRequest;
import semsem.chatbot.model.dto.response.ConversationResponse;
import semsem.chatbot.domain.conversation.Conversation;
import semsem.chatbot.domain.conversation.ConversationStatus;

import java.util.List;


public interface ConversationService {
    @Transactional
    ConversationResponse createConversation(String userEmail, CreateConversationRequest request);

    @Transactional(readOnly = true)
    ConversationResponse getConversation(String userEmail, Long conversationId, boolean includeMessages);

    @Transactional(readOnly = true)
    Page<ConversationResponse> getUserConversations(String userEmail, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ConversationResponse> getUserConversationsByStatus(
            Long userId,
            ConversationStatus status,
            Pageable pageable
    );

    @Transactional
    ConversationResponse updateConversation(
            String userEmail,
            Long conversationId,
            UpdateConversationRequest request
    );


    @Transactional
    void archiveConversation(String userEmail, Long conversationId);

    @Transactional
    void deleteConversation(String userEmail, Long conversationId);

    @Transactional(readOnly = true)
    List<ConversationResponse> searchConversations(String userEmail, String keyword);

    @Transactional
    void updateConversationSummary(Long conversationId, String summary);

    @Transactional(readOnly = true)
    long getConversationCount(Long userId);

    @Transactional(readOnly = true)
    long getConversationCountByStatus(Long userId, ConversationStatus status);

    @Transactional(readOnly = true)
    Conversation getConversationEntity(Long conversationId);

}