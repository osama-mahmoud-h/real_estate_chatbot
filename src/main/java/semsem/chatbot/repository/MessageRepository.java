package semsem.chatbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.chatbot.model.entity.Message;
import semsem.chatbot.model.enums.MessageRole;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByMessageId(Long messageId);

    boolean existsByMessageId(Long messageId);

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId ORDER BY m.createdAt ASC")
    Page<Message> findByConversationIdPaged(@Param("conversationId") Long conversationId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId AND m.role = :role ORDER BY m.createdAt ASC")
    List<Message> findByConversationIdAndRole(
            @Param("conversationId") Long conversationId,
            @Param("role") MessageRole role
    );

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId ORDER BY m.createdAt DESC")
    List<Message> findRecentMessages(@Param("conversationId") Long conversationId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Message> findLastMessageByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.conversationId = :conversationId")
    long countByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.conversationId = :conversationId AND m.role = :role")
    long countByConversationIdAndRole(
            @Param("conversationId") Long conversationId,
            @Param("role") MessageRole role
    );

    @Query("SELECT COALESCE(SUM(m.totalTokens), 0) FROM Message m WHERE m.conversation.conversationId = :conversationId")
    int sumTotalTokensByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT AVG(m.latencyMs) FROM Message m WHERE m.conversation.conversationId = :conversationId AND m.role = 'ASSISTANT'")
    Double avgLatencyByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT m FROM Message m WHERE m.content LIKE %:keyword% AND m.conversation.conversationId = :conversationId")
    List<Message> searchByContentInConversation(
            @Param("keyword") String keyword,
            @Param("conversationId") Long conversationId
    );

    @Query("SELECT m FROM Message m WHERE m.conversation.conversationId = :conversationId AND m.createdAt > :since ORDER BY m.createdAt ASC")
    List<Message> findMessagesSince(
            @Param("conversationId") Long conversationId,
            @Param("since") Instant since
    );

    @Modifying
    @Query("DELETE FROM Message m WHERE m.conversation.conversationId = :conversationId")
    int deleteByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT m FROM Message m WHERE m.parentMessageId = :parentMessageId ORDER BY m.createdAt ASC")
    List<Message> findByParentMessageId(@Param("parentMessageId") Long parentMessageId);
}
