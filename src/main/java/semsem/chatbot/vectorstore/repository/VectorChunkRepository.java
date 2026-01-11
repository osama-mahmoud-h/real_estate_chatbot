package semsem.chatbot.vectorstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.chatbot.vectorstore.entity.VectorChunkEntity;

import java.util.List;

/**
 * Repository for vector chunk operations.
 */
@Repository
public interface VectorChunkRepository extends JpaRepository<VectorChunkEntity, String> {

    List<VectorChunkEntity> findByDocumentId(String documentId);

    @Modifying
    @Query("DELETE FROM VectorChunkEntity v WHERE v.documentId = :documentId")
    void deleteByDocumentId(@Param("documentId") String documentId);

    long countByDocumentId(String documentId);

    // Note: Similarity search queries should be implemented using native queries
    // or Spring AI's PgVectorStore for proper vector operations
    // Example native query (requires pgvector extension):
    // @Query(value = "SELECT * FROM vector_chunks ORDER BY embedding <=> :query LIMIT :limit", nativeQuery = true)
    // List<VectorChunkEntity> findSimilar(@Param("query") String queryVector, @Param("limit") int limit);
}
