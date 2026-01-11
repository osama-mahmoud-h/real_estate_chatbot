package semsem.chatbot.service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.model.enums.LLMProvider;

import java.util.Map;

/**
 * Factory for creating LLM service instances.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMFactory {

    private final Map<String, LLMService> providers;

    public LLMService getService(LLMProvider provider) {
        // TODO: Implement provider selection
        return getService(provider.name().toLowerCase());
    }

    public LLMService getService(String providerName) {
        LLMService service = providers.get(providerName);
        if (service == null) {
            log.warn("Provider {} not found, using default", providerName);
            return getDefaultService();
        }
        return service;
    }

    public LLMService getDefaultService() {
        // TODO: Return default provider (configured)
        return providers.values().stream().findFirst().orElse(null);
    }

    public void registerProvider(String name, LLMService service) {
        providers.put(name, service);
    }
}
