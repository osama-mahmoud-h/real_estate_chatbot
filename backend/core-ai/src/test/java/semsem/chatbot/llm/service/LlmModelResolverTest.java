package semsem.chatbot.llm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import semsem.chatbot.llm.domain.entity.ChatLlmModel;
import semsem.chatbot.llm.domain.enums.ChatModelCapability;
import semsem.chatbot.llm.domain.entity.provider.CloudLlmProvider;
import semsem.chatbot.llm.domain.entity.FlowStepLlmConfig;
import semsem.chatbot.llm.domain.entity.GenerationParameters;
import semsem.chatbot.llm.domain.entity.provider.LocalLlmProvider;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;
import semsem.chatbot.llm.repository.FlowStepLlmConfigRepository;
import semsem.chatbot.llm.repository.LlmModelRepository;
import semsem.chatbot.llm.repository.LlmProviderRepository;
import semsem.chatbot.orchestration.common.FlowNames;
import semsem.chatbot.orchestration.dbchat.state.DbChatNodes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LlmModelResolverTest {

    @Mock
    private LlmProviderRepository providerRepository;

    @Mock
    private LlmModelRepository modelRepository;

    @Mock
    private FlowStepLlmConfigRepository flowStepConfigRepository;

    private LlmModelResolver modelResolver;
    private CloudLlmProvider openRouterProvider;
    private ChatLlmModel gemma31b;
    private ChatLlmModel gemma26b;

    @BeforeEach
    void setUp() {
        modelResolver = new LlmModelResolver(
                providerRepository, modelRepository, flowStepConfigRepository,
                "openrouter_gemma31b", "cohere_embed_multilingual_v3");

        openRouterProvider = CloudLlmProvider.create(
                "openrouter", "OpenRouter", "https://openrouter.ai/api/v1", "sk-or-test-key");

        gemma31b = openRouterProvider.registerModel(ChatLlmModel.of(
                "openrouter_gemma31b",
                "google/gemma-3-1b-it",
                32768,
                8192,
                GenerationParameters.of(4096, 0.0, 0.95),
                List.of(ChatModelCapability.STREAMING, ChatModelCapability.JSON_MODE),
                Map.of("top_k", 40)
        ));

        gemma26b = openRouterProvider.registerModel(ChatLlmModel.of(
                "openrouter_gemma26b",
                "google/gemma-2-27b-it",
                131072,
                8192,
                GenerationParameters.of(4096, 0.2, 0.9),
                List.of(ChatModelCapability.STREAMING, ChatModelCapability.JSON_MODE, ChatModelCapability.FUNCTION_CALLING),
                Map.of("top_k", 50)
        ));
    }

    @Test
    void resolveForStep_returnsStepAssignedModelAndMergesOverrides() {
        FlowStepLlmConfig stepConfig = FlowStepLlmConfig.assignWithOverrides(
                FlowNames.DB_CHAT,
                DbChatNodes.ENTITY_EXTRACTOR,
                gemma26b,
                GenerationParameters.of(2048, 0.0, null),
                Map.of("seed", 42)
        );
        when(flowStepConfigRepository.findActiveStepConfig(FlowNames.DB_CHAT, DbChatNodes.ENTITY_EXTRACTOR))
                .thenReturn(Optional.of(stepConfig));

        ResolvedChatModel resolved = modelResolver.resolveForStep(FlowNames.DB_CHAT, DbChatNodes.ENTITY_EXTRACTOR);

        assertThat(resolved.modelKey()).isEqualTo("openrouter_gemma26b");
        assertThat(resolved.modelName()).isEqualTo("google/gemma-2-27b-it");
        assertThat(resolved.providerKey()).isEqualTo("openrouter");
        assertThat(resolved.apiKey()).isEqualTo("sk-or-test-key");
        assertThat(resolved.maxTokens()).isEqualTo(2048); // overridden
        assertThat(resolved.temperature()).isEqualTo(0.0); // overridden
        assertThat(resolved.topP()).isEqualTo(0.9); // inherited from gemma26b
        assertThat(resolved.extraConfig()).containsEntry("top_k", 50).containsEntry("seed", 42);
    }

    @Test
    void resolveForStep_fallsBackToEnvDefaultKeyWhenStepNotConfigured() {
        when(flowStepConfigRepository.findActiveStepConfig(FlowNames.DB_CHAT, "unconfigured_step"))
                .thenReturn(Optional.empty());
        when(modelRepository.findEnabledChatModelByKey("openrouter_gemma31b"))
                .thenReturn(Optional.of(gemma31b));

        ResolvedChatModel resolved = modelResolver.resolveForStep(FlowNames.DB_CHAT, "unconfigured_step");

        assertThat(resolved.modelKey()).isEqualTo("openrouter_gemma31b");
        assertThat(resolved.modelName()).isEqualTo("google/gemma-3-1b-it");
        assertThat(resolved.maxTokens()).isEqualTo(4096);
        assertThat(resolved.apiKey()).isEqualTo("sk-or-test-key");
        assertThat(openRouterProvider.isAvailable()).isTrue();
    }

    @Test
    void localProvider_isAvailableWithoutApiKey() {
        LocalLlmProvider ollama = LocalLlmProvider.create(
                "ollama", "Ollama Local", "http://localhost:11434", 120, "5m");

        assertThat(ollama.isLocal()).isTrue();
        assertThat(ollama.isCloud()).isFalse();
        assertThat(ollama.getApiKey()).isNull();
        assertThat(ollama.isAvailable()).isTrue();
    }
}
