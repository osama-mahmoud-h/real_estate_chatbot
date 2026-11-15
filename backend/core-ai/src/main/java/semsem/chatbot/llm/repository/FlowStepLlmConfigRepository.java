package semsem.chatbot.llm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import semsem.chatbot.llm.domain.entity.FlowStepLlmConfig;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlowStepLlmConfigRepository extends JpaRepository<FlowStepLlmConfig, Long> {

    @Query("""
            SELECT c FROM FlowStepLlmConfig c
            JOIN FETCH c.chatModel m
            JOIN FETCH m.provider p
            WHERE LOWER(c.flowName) = LOWER(:flowName)
              AND LOWER(c.stepName) = LOWER(:stepName)
              AND c.enabled = true
              AND m.enabled = true
              AND p.enabled = true
            """)
    Optional<FlowStepLlmConfig> findActiveStepConfig(
            @Param("flowName") String flowName,
            @Param("stepName") String stepName);

    Optional<FlowStepLlmConfig> findByFlowNameIgnoreCaseAndStepNameIgnoreCase(String flowName, String stepName);

    List<FlowStepLlmConfig> findByFlowNameIgnoreCase(String flowName);
}
