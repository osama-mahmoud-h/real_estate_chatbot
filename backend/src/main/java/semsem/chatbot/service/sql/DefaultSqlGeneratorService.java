package semsem.chatbot.service.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import semsem.chatbot.config.SchemaDataSourceConfig;
import semsem.chatbot.config.SchemaDataSourceProperties;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;
import semsem.chatbot.prompt.PromptTemplate;
import semsem.chatbot.prompt.loader.PromptDefinition;
import semsem.chatbot.prompt.loader.PromptDefinitionsLoader;
import semsem.chatbot.prompt.loader.PromptRegistry;
import semsem.chatbot.service.llm.gateway.StructuredLLMGateway;
import semsem.chatbot.service.schema.SchemaMetadataExtractor;
import semsem.chatbot.service.schema.model.SchemaMetadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of SqlGeneratorService.
 * Uses LLM with sql-generator prompt and dynamically loaded schema.
 * Supports both static file and dynamic database schema loading.
 */
@Slf4j
@Service
public class DefaultSqlGeneratorService implements SqlGeneratorService {

    private final StructuredLLMGateway llmGateway;
    private final PromptRegistry promptRegistry;
    private final PromptDefinitionsLoader definitionsLoader;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Autowired(required = false)
    private SchemaMetadataExtractor schemaExtractor;

    @Autowired(required = false)
    private SchemaDataSourceConfig schemaDataSourceConfig;

    @Value("${chatbot.schema.path:classpath:db/v1_real_estate_schema_ddl.sql}")
    private String schemaPath;

    private static final String PROMPT_NAME = "sql-generator";

    private String cachedSchema;
    private SchemaMetadata cachedSchemaMetadata;
    private Instant cacheExpiry;

    public DefaultSqlGeneratorService(StructuredLLMGateway llmGateway,
                                       PromptRegistry promptRegistry,
                                       PromptDefinitionsLoader definitionsLoader,
                                       ObjectMapper objectMapper,
                                       ResourceLoader resourceLoader) {
        this.llmGateway = llmGateway;
        this.promptRegistry = promptRegistry;
        this.definitionsLoader = definitionsLoader;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

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
            Prompt prompt = buildPrompt(intent, entities, userQuery);
            SqlGeneratorOutput output = llmGateway.invokeStructured(prompt, SqlGeneratorOutput.class);

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
        refreshSchemaIfNeeded();
        return cachedSchema;
    }

    private void loadSchema() {
        if (isDynamicSchemaEnabled()) {
            loadSchemaFromDatabase();
        } else {
            loadSchemaFromFile();
        }
    }

    private boolean isDynamicSchemaEnabled() {
        return schemaExtractor != null && schemaDataSourceConfig != null
                && schemaDataSourceConfig.getActiveProperties().isPresent();
    }

    private void loadSchemaFromDatabase() {
        try {
            SchemaDataSourceProperties props = schemaDataSourceConfig.getActiveProperties()
                    .orElseThrow(() -> new IllegalStateException("No active schema datasource"));

            log.info("Loading schema dynamically from database: type={}, schema={}",
                    props.getType().getDisplayName(),
                    props.getEffectiveSchema());

            cachedSchemaMetadata = schemaExtractor.extract(props);
            cachedSchema = cachedSchemaMetadata.toLLMDescription();

            // Set cache expiry
            long ttl = props.getCacheTtlSeconds();
            cacheExpiry = ttl > 0 ? Instant.now().plusSeconds(ttl) : null;

            log.info("Loaded {} tables from database, cache TTL: {}s",
                    cachedSchemaMetadata.getTables().size(),
                    ttl);

        } catch (Exception e) {
            log.error("Failed to load schema from database, falling back to file: {}",
                    e.getMessage());
            loadSchemaFromFile();
        }
    }

    private void loadSchemaFromFile() {
        try {
            Resource resource = resourceLoader.getResource(schemaPath);
            cachedSchema = resource.getContentAsString(StandardCharsets.UTF_8);
            cachedSchemaMetadata = null;
            cacheExpiry = null;
            log.info("Loaded database schema from file: {}", schemaPath);
        } catch (IOException e) {
            log.error("Failed to load schema from {}: {}", schemaPath, e.getMessage());
            cachedSchema = "Schema not available";
        }
    }

    private void refreshSchemaIfNeeded() {
        if (isDynamicSchemaEnabled() && cacheExpiry != null && Instant.now().isAfter(cacheExpiry)) {
            log.debug("Schema cache expired, refreshing...");
            loadSchemaFromDatabase();
        }
    }

    private Prompt buildPrompt(QueryAnalyzerOutput.IntentResult intent,
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

        return new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt)));
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