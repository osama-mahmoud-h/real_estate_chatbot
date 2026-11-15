package semsem.chatbot.llm.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import semsem.chatbot.llm.domain.dto.ResolvedChatModel;
import semsem.chatbot.llm.domain.entity.ChatLlmModel;
import semsem.chatbot.llm.domain.entity.GenerationParameters;
import semsem.chatbot.llm.domain.entity.provider.CloudLlmProvider;
import semsem.chatbot.llm.domain.entity.provider.LocalLlmProvider;
import semsem.chatbot.llm.domain.enums.ChatModelCapability;
import semsem.chatbot.llm.factory.strategy.AnthropicChatModelStrategy;
import semsem.chatbot.llm.factory.strategy.GeminiChatModelStrategy;
import semsem.chatbot.llm.factory.strategy.HuggingFaceChatModelStrategy;
import semsem.chatbot.llm.factory.strategy.OllamaChatModelStrategy;
import semsem.chatbot.llm.factory.strategy.OpenAiChatModelStrategy;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicChatModelFactoryTest {

    private DynamicChatModelFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DynamicChatModelFactory(List.of(
                new OpenAiChatModelStrategy(),
                new GeminiChatModelStrategy(),
                new AnthropicChatModelStrategy(),
                new OllamaChatModelStrategy(),
                new HuggingFaceChatModelStrategy()
        ));
    }

    @Test
    void getOrCreate_dispatchesToOpenAiStrategyForOpenRouterCohereAndTogetherAi() {
        CloudLlmProvider openRouter = CloudLlmProvider.create(
                "openrouter", "OpenRouter", "https://openrouter.ai/api/v1", "sk-or-test-key"
        );

        ChatLlmModel model = openRouter.registerModel(ChatLlmModel.of(
                "openrouter_gemma31b", "google/gemma-3-1b-it", 32768, 8192,
                GenerationParameters.of(4096, 0.0, 0.9), List.of(ChatModelCapability.STREAMING), Map.of()
        ));

        ChatModel chatModel = factory.getOrCreate(ResolvedChatModel.fromDefault(model));

        assertThat(chatModel).isInstanceOf(OpenAiChatModel.class);
        assertThat(factory.getOrCreate(ResolvedChatModel.fromDefault(model))).isSameAs(chatModel);
    }

    @Test
    void getOrCreate_dispatchesToGeminiStrategyForGemini() {
        CloudLlmProvider gemini = CloudLlmProvider.create(
                "gemini", "Google Gemini", "https://generativelanguage.googleapis.com", "gemini-test-key"
        );

        ChatLlmModel model = gemini.registerModel(ChatLlmModel.of(
                "gemini_flash", "gemini-2.5-flash", 1048576, 8192,
                GenerationParameters.of(8192, 0.0, 0.95),
                List.of(ChatModelCapability.STREAMING, ChatModelCapability.JSON_MODE),
                Map.of("top_k", 40)
        ));

        ChatModel chatModel = factory.getOrCreate(ResolvedChatModel.fromDefault(model));

        assertThat(chatModel).isInstanceOf(GoogleGenAiChatModel.class);
    }

    @Test
    void getOrCreate_dispatchesToNativeAnthropicStrategyForAnthropic() {
        CloudLlmProvider anthropic = CloudLlmProvider.create(
                "anthropic", "Anthropic", "https://api.anthropic.com", "sk-ant-test-key"
        );

        ChatLlmModel claude = anthropic.registerModel(ChatLlmModel.of(
                "anthropic_claude_sonnet", "claude-3-7-sonnet-latest", 200000, 8192,
                GenerationParameters.of(4096, 0.0, 0.9),
                List.of(ChatModelCapability.STREAMING, ChatModelCapability.VISION),
                Map.of()
        ));

        ChatModel chatModel = factory.getOrCreate(ResolvedChatModel.fromDefault(claude));

        assertThat(chatModel).isInstanceOf(AnthropicChatModel.class);
    }

    @Test
    void getOrCreate_dispatchesToNativeOllamaStrategyForOllama() {
        LocalLlmProvider ollama = LocalLlmProvider.create(
                "ollama", "Ollama Local", "http://localhost:11434", 120, "10m"
        );

        ChatLlmModel llama = ollama.registerModel(ChatLlmModel.of(
                "ollama_llama3_2", "llama3.2", 131072, 4096,
                GenerationParameters.of(4096, 0.0, 0.9),
                List.of(ChatModelCapability.STREAMING),
                Map.of()
        ));

        ChatModel chatModel = factory.getOrCreate(ResolvedChatModel.fromDefault(llama));

        assertThat(chatModel).isInstanceOf(OllamaChatModel.class);
    }

    @Test
    void getOrCreate_dispatchesToNativeHuggingFaceStrategyForHuggingFace() {
        CloudLlmProvider hf = CloudLlmProvider.create(
                "huggingface", "HuggingFace", "https://api-inference.huggingface.co/models/meta-llama/Llama-3.2-3B-Instruct", "hf_test_token"
        );

        ChatLlmModel hfModel = hf.registerModel(ChatLlmModel.of(
                "hf_llama3_2", "meta-llama/Llama-3.2-3B-Instruct", 32768, 4096,
                GenerationParameters.of(2048, 0.0, 0.9),
                List.of(ChatModelCapability.STREAMING),
                Map.of()
        ));

        ChatModel chatModel = factory.getOrCreate(ResolvedChatModel.fromDefault(hfModel));

        assertThat(chatModel).isInstanceOf(HuggingfaceChatModel.class);
    }

    @Test
    void getOrCreate_dispatchesToOpenAiStrategyForLocalVllmVlm() {
        LocalLlmProvider vllm = LocalLlmProvider.create(
                "vllm", "vLLM Local Server", "http://localhost:8000/v1", 120, "10m"
        );

        ChatLlmModel vlmModel = vllm.registerModel(ChatLlmModel.of(
                "vllm_qwen25_vl", "Qwen/Qwen2.5-VL-7B-Instruct", 32768, 4096,
                GenerationParameters.of(4096, 0.0, 0.9),
                List.of(ChatModelCapability.STREAMING, ChatModelCapability.VISION),
                Map.of()
        ));

        ResolvedChatModel resolved = ResolvedChatModel.fromDefault(vlmModel);
        ChatModel chatModel = factory.getOrCreate(resolved);

        assertThat(chatModel).isInstanceOf(OpenAiChatModel.class);
        assertThat(resolved.requireApiKey()).isEqualTo("local-no-key");
        assertThat(resolved.effectiveExtraBody()).containsEntry("keep_alive", "10m");
        assertThat(vlmModel.supportsVision()).isTrue();
    }
}
