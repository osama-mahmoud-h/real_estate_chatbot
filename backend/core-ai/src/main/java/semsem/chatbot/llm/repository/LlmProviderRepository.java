package semsem.chatbot.llm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semsem.chatbot.llm.domain.entity.provider.LlmProvider;

import java.util.Optional;

@Repository
public interface LlmProviderRepository extends JpaRepository<LlmProvider, Long> {

    Optional<LlmProvider> findByProviderKeyIgnoreCase(String providerKey);

    boolean existsByProviderKeyIgnoreCase(String providerKey);
}
