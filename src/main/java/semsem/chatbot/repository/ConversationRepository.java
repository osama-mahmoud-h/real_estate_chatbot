package semsem.chatbot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.chatbot.model.entity.Conversation;
import semsem.chatbot.model.enums.ConversationStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByConversationId(String conversationId);

    boolean existsByConversationId(String conversationId);

    @Query("SELECT c FROM Conversation c WHERE c.appUser.userId = :userId ORDER BY c.updatedAt DESC")
    Page<Conversation> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE c.appUser.userId = :userId AND c.status = :status ORDER BY c.updatedAt DESC")
    Page<Conversation> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") ConversationStatus status,
            Pageable pageable
    );

    @Query("SELECT c FROM Conversation c WHERE c.appUser.userId = :userId AND c.status = :status")
    List<Conversation> findAllByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") ConversationStatus status
    );

    @Query("SELECT c FROM Conversation c WHERE c.appUser.email = :email ORDER BY c.updatedAt DESC")
    Page<Conversation> findByUserEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE c.appUser.email = :email AND c.conversationId = :conversationId")
    Optional<Conversation> findByUserEmailAndConversationId(
            @Param("email") String email,
            @Param("conversationId") String conversationId
    );

    @Query("SELECT c FROM Conversation c WHERE c.title LIKE %:keyword% AND c.appUser.userId = :userId")
    List<Conversation> searchByTitleAndUserId(
            @Param("keyword") String keyword,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.appUser.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.appUser.userId = :userId AND c.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ConversationStatus status);

    @Modifying
    @Query("UPDATE Conversation c SET c.status = :status, c.updatedAt = :updatedAt WHERE c.conversationId = :conversationId")
    int updateStatus(
            @Param("conversationId") String conversationId,
            @Param("status") ConversationStatus status,
            @Param("updatedAt") Instant updatedAt
    );

    @Modifying
    @Query("UPDATE Conversation c SET c.title = :title, c.updatedAt = :updatedAt WHERE c.conversationId = :conversationId")
    int updateTitle(
            @Param("conversationId") String conversationId,
            @Param("title") String title,
            @Param("updatedAt") Instant updatedAt
    );

    @Modifying
    @Query("UPDATE Conversation c SET c.summary = :summary, c.updatedAt = :updatedAt WHERE c.conversationId = :conversationId")
    int updateSummary(
            @Param("conversationId") String conversationId,
            @Param("summary") String summary,
            @Param("updatedAt") Instant updatedAt
    );

    @Modifying
    @Query("DELETE FROM Conversation c WHERE c.appUser.userId = :userId AND c.status = :status")
    int deleteByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ConversationStatus status);
}
