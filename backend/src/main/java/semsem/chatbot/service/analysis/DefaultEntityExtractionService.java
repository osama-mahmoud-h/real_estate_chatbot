package semsem.chatbot.service.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import semsem.chatbot.model.enums.MessageRole;
import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.prompt.PromptTemplate;
import semsem.chatbot.prompt.loader.PromptDefinition;
import semsem.chatbot.prompt.loader.PromptDefinitionsLoader;
import semsem.chatbot.prompt.loader.PromptRegistry;
import semsem.chatbot.service.llm.gateway.StructuredLLMGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of EntityExtractionService.
 * Uses LLM with query-analyzer prompt to extract intent and entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultEntityExtractionService implements EntityExtractionService {

    private final StructuredLLMGateway llmGateway;
    private final PromptRegistry promptRegistry;
    private final PromptDefinitionsLoader definitionsLoader;

    private static final String PROMPT_NAME = "query-analyzer";
    private static final int MAX_HISTORY_MESSAGES = 5;

    @Override
    public QueryAnalyzerOutput extract(String userQuery, String detectedLanguage, List<ChatMessage> conversationHistory) {
        log.debug("Extracting entities from query: {}", userQuery);

        try {
            Prompt prompt = buildPrompt(userQuery, detectedLanguage, conversationHistory);
            QueryAnalyzerOutput output = llmGateway.invokeStructured(prompt, QueryAnalyzerOutput.class);
            log.info("Extracted intent: {} (confidence: {})",
                    output.getIntent().getName(),
                    output.getIntent().getConfidence());

            return output;

        } catch (Exception e) {
            log.error("Failed to extract entities: {}", e.getMessage(), e);
            return createFallbackOutput(userQuery);
        }
    }

    /**
     * Assembles a role-tagged prompt: {@code [System(instructions + few-shots), *history, Human(query)]}.
     * Prior turns are carried as native {@link UserMessage}/{@link AssistantMessage} rather than being
     * flattened into a text blob, so the model sees real conversational structure.
     */
    private Prompt buildPrompt(String userQuery, String detectedLanguage, List<ChatMessage> conversationHistory) {
        PromptDefinition promptDef = promptRegistry.getOrThrow(PROMPT_NAME);

        Map<String, Object> variables = Map.of(
                "user_query", userQuery,
                "detected_language", detectedLanguage,
                "intent_definitions", definitionsLoader.get(PROMPT_NAME, "intent-definitions"),
                "entity_types", definitionsLoader.get(PROMPT_NAME, "entity-types"),
                "examples", definitionsLoader.get(PROMPT_NAME, "examples")
        );

        String systemPrompt = new PromptTemplate(promptDef.getSystemPrompt()).format(variables);
        String userPrompt = new PromptTemplate(promptDef.getUserPrompt()).format(variables);

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));
        messages.addAll(toHistoryMessages(conversationHistory));
        messages.add(new UserMessage(userPrompt));

        return new Prompt(messages);
    }

    /**
     * Maps the last {@link #MAX_HISTORY_MESSAGES} prior turns to native Spring AI messages,
     * preserving author role (user vs assistant).
     */
    private List<Message> toHistoryMessages(List<ChatMessage> history) {
        return Optional.ofNullable(history)
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    int startIndex = Math.max(0, list.size() - MAX_HISTORY_MESSAGES);
                    return list.subList(startIndex, list.size()).stream()
                            .map(this::toMessage)
                            .toList();
                })
                .orElseGet(List::of);
    }

    private Message toMessage(ChatMessage message) {
        return message.getRole() == MessageRole.ASSISTANT
                ? new AssistantMessage(message.getContent())
                : new UserMessage(message.getContent());
    }

    private QueryAnalyzerOutput createFallbackOutput(String userQuery) {
        return QueryAnalyzerOutput.builder()
                .intent(QueryAnalyzerOutput.IntentResult.builder()
                        .name("GENERAL_QUESTION")
                        .confidence(0.5)
                        .requiresSql(false)
                        .reasoning("Fallback due to parsing error")
                        .build())
                .entities(QueryAnalyzerOutput.ExtractedEntities.builder().build())
                .build();
    }
}