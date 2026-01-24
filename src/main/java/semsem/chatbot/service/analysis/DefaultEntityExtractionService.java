package semsem.chatbot.service.analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.prompt.PromptTemplate;
import semsem.chatbot.prompt.loader.PromptDefinition;
import semsem.chatbot.prompt.loader.PromptDefinitionsLoader;
import semsem.chatbot.prompt.loader.PromptRegistry;
import semsem.chatbot.service.llm.LLMService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of EntityExtractionService.
 * Uses LLM with query-analyzer prompt to extract intent and entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultEntityExtractionService implements EntityExtractionService {

    private final LLMService llmService;
    private final PromptRegistry promptRegistry;
    private final PromptDefinitionsLoader definitionsLoader;
    private final ObjectMapper objectMapper;

    private static final String PROMPT_NAME = "query-analyzer";
    private static final int MAX_HISTORY_MESSAGES = 5;

    @Override
    public QueryAnalyzerOutput extract(String userQuery, String detectedLanguage, List<ChatMessage> conversationHistory) {
        log.debug("Extracting entities from query: {}", userQuery);

        try {
            String prompt = buildPrompt(userQuery, detectedLanguage, conversationHistory);
            String llmResponse = llmService.generate(prompt);
            log.debug("LLM response: {}", llmResponse);

            QueryAnalyzerOutput output = parseResponse(llmResponse);
            log.info("Extracted intent: {} (confidence: {})",
                    output.getIntent().getName(),
                    output.getIntent().getConfidence());

            return output;

        } catch (Exception e) {
            log.error("Failed to extract entities: {}", e.getMessage(), e);
            return createFallbackOutput(userQuery);
        }
    }

    private String buildPrompt(String userQuery, String detectedLanguage, List<ChatMessage> conversationHistory) {
        PromptDefinition promptDef = promptRegistry.getOrThrow(PROMPT_NAME);

        Map<String, Object> variables = Map.of(
                "user_query", userQuery,
                "detected_language", detectedLanguage,
                "conversation_history", formatConversationHistory(conversationHistory),
                "intent_definitions", definitionsLoader.get(PROMPT_NAME, "intent-definitions"),
                "entity_types", definitionsLoader.get(PROMPT_NAME, "entity-types"),
                "examples", definitionsLoader.get(PROMPT_NAME, "examples")
        );

        String systemPrompt = new PromptTemplate(promptDef.getSystemPrompt()).format(variables);
        String userPrompt = new PromptTemplate(promptDef.getUserPrompt()).format(variables);

        return systemPrompt + "\n\n" + userPrompt;
    }

    private String formatConversationHistory(List<ChatMessage> messages) {
        return Optional.ofNullable(messages)
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    int startIndex = Math.max(0, list.size() - MAX_HISTORY_MESSAGES);
                    return list.subList(startIndex, list.size()).stream()
                            .map(m -> m.getRole() + ": " + m.getContent())
                            .collect(Collectors.joining("\n"));
                })
                .orElse("No previous conversation.");
    }

    private QueryAnalyzerOutput parseResponse(String response) throws JsonProcessingException {
        String json = extractJson(response);
        return objectMapper.readValue(json, QueryAnalyzerOutput.class);
    }

    private String extractJson(String response) {
        String cleaned = response.trim();

        // Remove markdown code blocks
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
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