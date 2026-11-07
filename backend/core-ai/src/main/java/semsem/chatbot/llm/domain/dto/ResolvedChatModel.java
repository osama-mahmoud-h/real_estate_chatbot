package semsem.chatbot.llm.domain.dto;

import semsem.chatbot.llm.domain.entity.ChatLlmModel;
import semsem.chatbot.llm.domain.entity.FlowStepLlmConfig;
import semsem.chatbot.llm.domain.entity.GenerationParameters;
import semsem.chatbot.llm.domain.entity.provider.LlmProvider;

import java.util.Map;

/**
 * Immutable domain value object representing the resolved chat model, its provider
 * (with hydrated runtime credentials), and effective generation parameters for an execution step.
 */
public record ResolvedChatModel(
        ChatLlmModel model,
        LlmProvider provider,
        GenerationParameters parameters,
        Map<String, Object> extraConfig
) {

    public static ResolvedChatModel fromDefault(ChatLlmModel model) {
        return new ResolvedChatModel(
                model,
                model.getProvider(),
                model.getGenerationParameters(),
                model.getExtraConfig()
        );
    }

    public static ResolvedChatModel fromStepConfig(FlowStepLlmConfig stepConfig) {
        ChatLlmModel model = stepConfig.getChatModel();
        return new ResolvedChatModel(
                model,
                model.getProvider(),
                stepConfig.effectiveParameters(),
                stepConfig.effectiveExtraConfig()
        );
    }

    public String modelKey() {
        return model.getModelKey();
    }

    public String modelName() {
        return model.getModelName();
    }

    public String providerKey() {
        return provider.getProviderKey();
    }

    public String baseUrl() {
        return provider.getBaseUrl();
    }

    public String apiKey() {
        return provider.getApiKey();
    }

    public String requireApiKey() {
        return provider.requireApiKey();
    }

    public Map<String, Object> effectiveExtraBody() {
        return provider.mergeProviderOptions(extraConfig);
    }

    public int maxTokens() {
        return parameters.maxTokensOrDefault();
    }

    public double temperature() {
        return parameters.temperatureOrDefault();
    }

    public double topP() {
        return parameters.topPOrDefault();
    }

    public int timeoutSeconds() {
        return provider != null ? provider.getEffectiveTimeoutSeconds() : 120;
    }
}
