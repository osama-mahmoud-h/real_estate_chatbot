package semsem.chatbot.vectorstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/**
 * Entity for storing document chunks with vector embeddings.
 */
@Entity
@Table(name = "vector_chunks", indexes = {
        @Index(name = "idx_vector_chunks_document_id", columnList = "document_id"),
        @Index(name = "idx_vector_chunks_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorChunkEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "document_id", nullable = false, length = 36)
    private String documentId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    // Note: For pgvector, use native query or Spring AI VectorStore
    // The actual vector column is defined in migration script
    @Transient
    private float[] embedding;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
