package semsem.chatbot.service.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;
import semsem.chatbot.prompt.PromptTemplate;
import semsem.chatbot.prompt.loader.PromptDefinition;
import semsem.chatbot.prompt.loader.PromptDefinitionsLoader;
import semsem.chatbot.prompt.loader.PromptRegistry;
import semsem.chatbot.service.llm.LLMService;

import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of ResponseGeneratorService.
 * Uses LLM with response-generator prompt to create natural language responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultResponseGeneratorService implements ResponseGeneratorService {

    private final LLMService llmService;
    private final PromptRegistry promptRegistry;
    private final PromptDefinitionsLoader definitionsLoader;
    private final ObjectMapper objectMapper;

    private static final String PROMPT_NAME = "response-generator";
    private static final String DEFAULT_LANGUAGE = "English";

    @Override
    public String generate(String userQuery,
                           QueryAnalyzerOutput.IntentResult intent,
                           QueryAnalyzerOutput.ExtractedEntities entities,
                           SqlExecutorOutput sqlResults,
                           String detectedLanguage) {

        log.debug("Generating response for intent: {}",
                Optional.ofNullable(intent).map(QueryAnalyzerOutput.IntentResult::getName).orElse("unknown"));

        try {
            String prompt = buildPrompt(userQuery, intent, entities, sqlResults, detectedLanguage);
            String response = llmService.generate(prompt);

            log.debug("Generated response length: {} chars", response.length());
            return response;

        } catch (Exception e) {
            log.error("Failed to generate response: {}", e.getMessage(), e);
            return createFallbackResponse(sqlResults);
        }
    }

    private String buildPrompt(String userQuery,
                                QueryAnalyzerOutput.IntentResult intent,
                                QueryAnalyzerOutput.ExtractedEntities entities,
                                SqlExecutorOutput sqlResults,
                                String detectedLanguage) throws JsonProcessingException {

        PromptDefinition promptDef = promptRegistry.getOrThrow(PROMPT_NAME);

        String intentName = Optional.ofNullable(intent)
                .map(QueryAnalyzerOutput.IntentResult::getName)
                .orElse("GENERAL_QUESTION");

        String entitiesJson = Optional.ofNullable(entities)
                .map(this::toJson)
                .orElse("{}");

        String resultsJson = Optional.ofNullable(sqlResults)
                .map(SqlExecutorOutput::getResults)
                .map(this::toJson)
                .orElse("[]");

        int resultCount = Optional.ofNullable(sqlResults)
                .map(SqlExecutorOutput::getRowCount)
                .orElse(0);

        String language = Optional.ofNullable(detectedLanguage)
                .filter(l -> !l.isBlank())
                .orElse(DEFAULT_LANGUAGE);

        Map<String, Object> variables = Map.of(
                "user_query", userQuery,
                "intent", intentName,
                "entities", entitiesJson,
                "sql_results", resultsJson,
                "result_count", String.valueOf(resultCount),
                "detected_language", language,
                "response_patterns", definitionsLoader.get(PROMPT_NAME, "response-patterns"),
                "examples", definitionsLoader.get(PROMPT_NAME, "examples")
        );

        String systemPrompt = new PromptTemplate(promptDef.getSystemPrompt()).format(variables);
        String userPrompt = new PromptTemplate(promptDef.getUserPrompt()).format(variables);

        return systemPrompt + "\n\n" + userPrompt;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize object to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    private String createFallbackResponse(SqlExecutorOutput sqlResults) {
        if (sqlResults == null || !sqlResults.isSuccess()) {
            return "I apologize, but I encountered an issue processing your request. Please try again.";
        }

        int count = sqlResults.getRowCount();
        if (count == 0) {
            return "I couldn't find any results matching your criteria. Would you like to try a different search?";
        }

        return String.format("I found %d result(s) for your query.", count);
    }
}