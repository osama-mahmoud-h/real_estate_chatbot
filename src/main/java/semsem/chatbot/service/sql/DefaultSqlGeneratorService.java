package semsem.chatbot.service.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;
import semsem.chatbot.prompt.PromptTemplate;
import semsem.chatbot.prompt.loader.PromptDefinition;
import semsem.chatbot.prompt.loader.PromptDefinitionsLoader;
import semsem.chatbot.prompt.loader.PromptRegistry;
import semsem.chatbot.service.llm.LLMService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of SqlGeneratorService.
 * Uses LLM with sql-generator prompt and dynamically loaded schema.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultSqlGeneratorService implements SqlGeneratorService {

    private final LLMService llmService;
    private final PromptRegistry promptRegistry;
    private final PromptDefinitionsLoader definitionsLoader;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Value("${chatbot.schema.path:classpath:db/v1_real_estate_schema_ddl.sql}")
    private String schemaPath;

    private static final String PROMPT_NAME = "sql-generator";

    private String cachedSchema;

    @PostConstruct
    public void init() {
        loadSchema();
    }

    @Override
    public SqlGeneratorOutput generate(QueryAnalyzerOutput.IntentResult intent,
                                        QueryAnalyzerOutput.ExtractedEntities entities,
                                        String userQuery) {
        log.debug("Generating SQL for intent: {}", intent.getName());

        // Skip SQL generation for non-SQL intents
        if (!intent.isRequiresSql()) {
            log.debug("Intent {} does not require SQL", intent.getName());
            return createNoSqlOutput(intent.getName());
        }

        try {
            String prompt = buildPrompt(intent, entities, userQuery);
            String llmResponse = llmService.generate(prompt);
            log.debug("LLM response: {}", llmResponse);

            SqlGeneratorOutput output = parseResponse(llmResponse);

            // Validate safety
            if (!validateSafety(output)) {
                log.warn("Generated SQL failed safety validation");
                return createErrorOutput("Generated SQL failed safety validation");
            }

            log.info("Generated SQL for {} tables: {}",
                    Optional.ofNullable(output.getTablesUsed()).map(t -> t.size()).orElse(0),
                    output.getExplanation());

            return output;

        } catch (Exception e) {
            log.error("Failed to generate SQL: {}", e.getMessage(), e);
            return createErrorOutput(e.getMessage());
        }
    }

    @Override
    public String getSchemaDescription() {
        return cachedSchema;
    }

    private void loadSchema() {
        try {
            Resource resource = resourceLoader.getResource(schemaPath);
            cachedSchema = resource.getContentAsString(StandardCharsets.UTF_8);
            log.info("Loaded database schema from: {}", schemaPath);
        } catch (IOException e) {
            log.error("Failed to load schema from {}: {}", schemaPath, e.getMessage());
            cachedSchema = "Schema not available";
        }
    }

    private String buildPrompt(QueryAnalyzerOutput.IntentResult intent,
                                QueryAnalyzerOutput.ExtractedEntities entities,
                                String userQuery) throws JsonProcessingException {
        PromptDefinition promptDef = promptRegistry.getOrThrow(PROMPT_NAME);

        String entitiesJson = objectMapper.writeValueAsString(entities);

        Map<String, Object> variables = Map.of(
                "intent", intent.getName(),
                "entities", entitiesJson,
                "database_schema", cachedSchema,
                "user_query", userQuery,
                "enum_definitions", definitionsLoader.get(PROMPT_NAME, "enum-definitions"),
                "query_patterns", definitionsLoader.get(PROMPT_NAME, "query-patterns"),
                "examples", definitionsLoader.get(PROMPT_NAME, "examples")
        );

        String systemPrompt = new PromptTemplate(promptDef.getSystemPrompt()).format(variables);
        String userPrompt = new PromptTemplate(promptDef.getUserPrompt()).format(variables);

        return systemPrompt + "\n\n" + userPrompt;
    }

    private SqlGeneratorOutput parseResponse(String response) throws JsonProcessingException {
        String json = extractJson(response);
        return objectMapper.readValue(json, SqlGeneratorOutput.class);
    }

    private String extractJson(String response) {
        String cleaned = response.trim();

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

    private boolean validateSafety(SqlGeneratorOutput output) {
        if (output.getGeneratedSql() == null) {
            return false;
        }

        String sqlUpper = output.getGeneratedSql().toUpperCase();

        // Check for dangerous keywords
        return !sqlUpper.contains("INSERT")
                && !sqlUpper.contains("UPDATE")
                && !sqlUpper.contains("DELETE")
                && !sqlUpper.contains("DROP")
                && !sqlUpper.contains("TRUNCATE")
                && !sqlUpper.contains("ALTER")
                && !sqlUpper.contains("CREATE")
                && !sqlUpper.contains("GRANT")
                && !sqlUpper.contains("REVOKE");
    }

    private SqlGeneratorOutput createNoSqlOutput(String intentName) {
        return SqlGeneratorOutput.builder()
                .generatedSql(null)
                .isSafe(true)
                .explanation("Intent " + intentName + " does not require SQL query")
                .build();
    }

    private SqlGeneratorOutput createErrorOutput(String error) {
        return SqlGeneratorOutput.builder()
                .generatedSql(null)
                .isSafe(false)
                .explanation("Error: " + error)
                .build();
    }
}