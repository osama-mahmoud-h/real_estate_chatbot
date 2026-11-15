package semsem.chatbot.llm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.chatbot.llm.domain.entity.ChatLlmModel;
import semsem.chatbot.llm.domain.entity.EmbeddingLlmModel;
import semsem.chatbot.llm.domain.entity.LlmModel;

import java.util.Optional;

@Repository
public interface LlmModelRepository extends JpaRepository<LlmModel, Long> {

    Optional<LlmModel> findByModelKeyIgnoreCase(String modelKey);

    @Query("SELECT m FROM ChatLlmModel m JOIN FETCH m.provider WHERE LOWER(m.modelKey) = LOWER(:modelKey) AND m.enabled = true")
    Optional<ChatLlmModel> findEnabledChatModelByKey(@Param("modelKey") String modelKey);

    @Query("SELECT m FROM EmbeddingLlmModel m JOIN FETCH m.provider WHERE LOWER(m.modelKey) = LOWER(:modelKey) AND m.enabled = true")
    Optional<EmbeddingLlmModel> findEnabledEmbeddingModelByKey(@Param("modelKey") String modelKey);

    boolean existsByModelKeyIgnoreCase(String modelKey);
}
