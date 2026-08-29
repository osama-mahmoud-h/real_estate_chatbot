package semsem.chatbot.domain.conversation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import semsem.chatbot.domain.user.AppUser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root over its {@link Message}s. Every state change goes through a
 * method here so that updatedAt and tokenCount cannot drift apart from the
 * messages that caused them.
 */
@Entity
@Table(name = "conversations")
@Getter
@ToString(exclude = {"appUser", "messages"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation {

    private static final String DEFAULT_TITLE = "New Conversation";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_tokens_user_id"),
            referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser appUser;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConversationStatus status;

    @Column(name = "token_count")
    private int tokenCount;

    @Column(name = "summary")
    private String summary;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    private Conversation(AppUser owner, String title) {
        if (owner == null) {
            throw new IllegalArgumentException("A conversation needs an owner");
        }
        Instant now = Instant.now();
        this.appUser = owner;
        this.title = normalizeTitle(title);
        this.status = ConversationStatus.ACTIVE;
        this.tokenCount = 0;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Conversation start(AppUser owner, String title) {
        return new Conversation(owner, title);
    }

    /** Unmodifiable: add through {@link #addMessage} so the counters stay honest. */
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public Message addMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot add a null message");
        }
        message.assignTo(this);
        messages.add(message);
        tokenCount += message.getTokenUsage().total();
        touch();
        return message;
    }

    public void rename(String title) {
        this.title = normalizeTitle(title);
        touch();
    }

    public void summarize(String summary) {
        this.summary = summary;
        touch();
    }

    public void archive() {
        if (status == ConversationStatus.ARCHIVED) {
            return;
        }
        this.status = ConversationStatus.ARCHIVED;
        this.archivedAt = Instant.now();
        touch();
    }

    public void reactivate() {
        this.status = ConversationStatus.ACTIVE;
        this.archivedAt = null;
        touch();
    }

    public void changeStatus(ConversationStatus status) {
        if (status == null || status == this.status) {
            return;
        }
        if (status == ConversationStatus.ARCHIVED) {
            archive();
            return;
        }
        if (status == ConversationStatus.ACTIVE) {
            reactivate();
            return;
        }
        this.status = status;
        touch();
    }

    public boolean isOwnedBy(String email) {
        return appUser != null && appUser.getEmail() != null && appUser.getEmail().equals(email);
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static String normalizeTitle(String title) {
        return (title == null || title.isBlank()) ? DEFAULT_TITLE : title;
    }
}