package semsem.chatbot.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.model.dto.request.CreateMessageRequest;
import semsem.chatbot.model.dto.response.MessageResponse;
import semsem.chatbot.model.dto.response.MessageStatsResponse;
import semsem.chatbot.model.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public interface MessageService {
    @Transactional
    MessageResponse createMessage(Long conversationId, CreateMessageRequest request);

    @Transactional(readOnly = true)
    MessageResponse getMessage(Long messageId);

    @Transactional(readOnly = true)
    List<MessageResponse> getConversationMessages(Long conversationId);

    @Transactional(readOnly = true)
    Page<MessageResponse> getConversationMessagesPaged(Long conversationId, Pageable pageable);

    @Transactional(readOnly = true)
    List<MessageResponse> getConversationMessagesByRole(Long conversationId, MessageRole role);

    @Transactional(readOnly = true)
    List<MessageResponse> getRecentMessages(Long conversationId, int limit);

    @Transactional(readOnly = true)
    MessageResponse getLastMessage(Long conversationId);

    @Transactional
    MessageResponse updateMessage(Long messageId, CreateMessageRequest request);

    @Transactional
    void deleteMessage(Long messageId);

    @Transactional
    void deleteConversationMessages(Long conversationId);

    @Transactional(readOnly = true)
    long getMessageCount(Long conversationId);

    @Transactional(readOnly = true)
    long getMessageCountByRole(Long conversationId, MessageRole role);

    @Transactional(readOnly = true)
    int getTotalTokens(Long conversationId);

    @Transactional(readOnly = true)
    Double getAverageLatency(Long conversationId);

    @Transactional(readOnly = true)
    List<MessageResponse> searchMessages(Long conversationId, String keyword);

    @Transactional(readOnly = true)
    List<MessageResponse> getMessagesSince(Long conversationId, Instant since);

    @Transactional(readOnly = true)
    List<MessageResponse> getChildMessages(Long parentMessageId);

    @Transactional(readOnly = true)
    MessageStatsResponse getMessageStats(Long conversationId);
}