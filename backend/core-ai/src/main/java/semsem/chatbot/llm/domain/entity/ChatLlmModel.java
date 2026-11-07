package semsem.chatbot.llm.domain.entity;

import semsem.chatbot.llm.domain.enums.ChatModelCapability;
import semsem.chatbot.llm.domain.enums.ModelCapability;
import semsem.chatbot.llm.domain.enums.ModelType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Chat / generative LLM model entity (e.g. "openrouter_gemma31b" -> "google/gemma-3-1b-it").
 * Holds context limits, capabilities, and default {@link GenerationParameters}.
 */
@Entity
@DiscriminatorValue("CHAT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatLlmModel extends LlmModel {

    @Column(name = "context_window")
    private Integer contextWindow;

    @Column(name = "max_output_tokens")
    private Integer maxOutputTokens;

    @Embedded
    private GenerationParameters generationParameters = GenerationParameters.defaults();

    private ChatLlmModel(String modelKey,
                         String modelName,
                         int contextWindow,
                         int maxOutputTokens,
                         GenerationParameters generationParameters,
                         List<String> capabilities,
                         Map<String, Object> extraConfig) {
        super(modelKey, modelName, capabilities, extraConfig);
        this.contextWindow = contextWindow;
        this.maxOutputTokens = maxOutputTokens;
        this.generationParameters = generationParameters != null
                ? generationParameters
                : GenerationParameters.defaults();
    }

    public static ChatLlmModel of(String modelKey,
                                  String modelName,
                                  int contextWindow,
                                  int maxOutputTokens,
                                  GenerationParameters generationParameters,
                                  List<ChatModelCapability> capabilities) {
        return of(modelKey, modelName, contextWindow, maxOutputTokens,
                generationParameters, capabilities, Map.of());
    }

    public static ChatLlmModel of(String modelKey,
                                  String modelName,
                                  int contextWindow,
                                  int maxOutputTokens,
                                  GenerationParameters generationParameters,
                                  List<ChatModelCapability> capabilities,
                                  Map<String, Object> extraConfig) {
        List<String> capStrings = capabilities != null
                ? capabilities.stream().map(ChatModelCapability::getValue).toList()
                : List.of();
        return new ChatLlmModel(modelKey, modelName, contextWindow, maxOutputTokens,
                generationParameters, capStrings, extraConfig);
    }

    public GenerationParameters getGenerationParameters() {
        return generationParameters != null ? generationParameters : GenerationParameters.defaults();
    }

    public void updateGenerationParameters(GenerationParameters parameters) {
        this.generationParameters = parameters != null ? parameters : GenerationParameters.defaults();
        touch();
    }

    @Override
    public ModelType getType() {
        return ModelType.CHAT;
    }

    public int getContextWindow() {
        return contextWindow != null ? contextWindow : 0;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens != null ? maxOutputTokens : 0;
    }

    public boolean supportsStreaming() {
        return hasCapability(ChatModelCapability.STREAMING);
    }

    public boolean supportsFunctionCalling() {
        return hasCapability(ChatModelCapability.FUNCTION_CALLING);
    }

    public boolean supportsVision() {
        return hasCapability(ChatModelCapability.VISION);
    }

    public boolean supportsJsonMode() {
        return hasCapability(ChatModelCapability.JSON_MODE);
    }

    public boolean supportsSystemPrompt() {
        return hasCapability(ChatModelCapability.SYSTEM_PROMPT);
    }
}
