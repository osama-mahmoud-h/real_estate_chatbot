package semsem.chatbot.llm.factory.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;

/**
 * Strategy that builds a native {@link HuggingfaceChatModel} for HuggingFace Text Generation Inference (TGI) endpoints.
 */
@Slf4j
@Component
@Order(10)
public class HuggingFaceChatModelStrategy implements ChatModelBuilderStrategy {

    private static final String HUGGINGFACE_PROVIDER_KEY = "huggingface";

    @Override
    public boolean supports(ResolvedChatModel resolved) {
        return resolved != null && HUGGINGFACE_PROVIDER_KEY.equalsIgnoreCase(resolved.providerKey());
    }

    @Override
    public ChatModel build(ResolvedChatModel resolved) {
        try {
            if (resolved.baseUrl() == null || resolved.baseUrl().isBlank()) {
                throw new IllegalArgumentException("HuggingFace base URL is required");
            }
            log.debug("Building native HuggingfaceChatModel for model='{}', baseUrl='{}', maxNewTokens={}",
                    resolved.modelName(), resolved.baseUrl(), resolved.maxTokens());

            HuggingfaceChatModel chatModel = new HuggingfaceChatModel(
                    resolved.requireApiKey(),
                    resolved.baseUrl()
            );
            chatModel.setMaxNewTokens(resolved.maxTokens());
            return chatModel;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            log.error("HuggingfaceChatModel configuration error for model='{}': {}", resolved.modelName(), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected failure building HuggingfaceChatModel for model='{}': {}",
                    resolved.modelName(), ex.getMessage(), ex);
            throw new IllegalStateException("Failed to build HuggingfaceChatModel: " + ex.getMessage(), ex);
        }
    }
}
