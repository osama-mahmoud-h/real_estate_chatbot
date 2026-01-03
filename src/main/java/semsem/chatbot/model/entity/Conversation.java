package semsem.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import semsem.chatbot.model.enums.ConversationStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false, unique = true)
    private String conversationId;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_tokens_user_id"),
            referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser appUser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conversation_messages",
            joinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "conversation_id", foreignKey = @ForeignKey(name = "FK_conversations_messages_conversation_id")),
            inverseJoinColumns = @JoinColumn(name = "message_id", referencedColumnName = "message_id", foreignKey = @ForeignKey(name = "FK_conversation_message_conversation_id"))
    )
    private List<Message> messages;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConversationStatus status;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Store additional info as JSON

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "summary")
    private String summary;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

}