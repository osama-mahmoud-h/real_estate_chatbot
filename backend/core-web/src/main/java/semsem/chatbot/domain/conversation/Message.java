package semsem.chatbot.domain.conversation;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;
import semsem.chatbot.model.enums.MessageRole;

import java.time.Instant;

/**
 * A single turn in a {@link Conversation}. Lives inside that aggregate: instances
 * are created detached and attached by {@link Conversation#addMessage}, which is
 * what keeps the conversation's token count and updatedAt in step.
 */
@Entity
@Getter
@ToString(exclude = "conversation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages", indexes = {
        @Index(name = "idx_conversation_created", columnList = "conversation_id, created_at"),
        @Index(name = "idx_role_conversation", columnList = "role, conversation_id")
})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id",
            referencedColumnName = "conversation_id",
            nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_messages_conversation_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MessageRole role;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "provider_llm")
    private String providerLlm;

    @Column(name = "model_used")
    private String modelUsed;

    @Embedded
    private TokenUsage tokenUsage = TokenUsage.none();

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "parent_message_id")
    private Long parentMessageId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Store tool calls, function calls, etc.

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    private Message(MessageRole role, String content, Long parentMessageId, String metadata) {
        if (role == null) {
            throw new IllegalArgumentException("A message needs a role");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("A message needs content");
        }
        this.role = role;
        this.content = content;
        this.parentMessageId = parentMessageId;
        this.metadata = metadata;
        this.tokenUsage = TokenUsage.none();
    }

    public static Message from(MessageRole role, String content) {
        return new Message(role, content, null, null);
    }

    public static Message from(MessageRole role, String content, Long parentMessageId, String metadata) {
        return new Message(role, content, parentMessageId, metadata);
    }

    /**
     * Records what the model actually did. Setting a latency marks the message
     * processed, which is the pairing the old mapper had to remember by hand.
     */
    public void recordGeneration(String providerLlm, String modelUsed, TokenUsage usage, Long latencyMs) {
        this.providerLlm = providerLlm;
        this.modelUsed = modelUsed;
        this.tokenUsage = usage != null ? usage : TokenUsage.none();
        this.latencyMs = latencyMs;
        if (latencyMs != null) {
            this.processedAt = Instant.now();
        }
    }

    public void editContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("A message needs content");
        }
        this.content = content;
    }

    public void describeWith(String metadata) {
        this.metadata = metadata;
    }

    public TokenUsage getTokenUsage() {
        return tokenUsage != null ? tokenUsage : TokenUsage.none();
    }

    /** Called by {@link Conversation#addMessage} only. */
    void assignTo(Conversation conversation) {
        this.conversation = conversation;
    }
}