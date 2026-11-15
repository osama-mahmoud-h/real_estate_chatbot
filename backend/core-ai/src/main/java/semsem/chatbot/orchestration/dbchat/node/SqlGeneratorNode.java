package semsem.chatbot.orchestration.dbchat.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.common.FlowNames;
import semsem.chatbot.orchestration.dbchat.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlGeneratorOutput;
import semsem.chatbot.orchestration.dbchat.state.DbChatNodes;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;
import semsem.chatbot.prompt.builder.ChatPromptFactory;
import semsem.chatbot.llm.gateway.StructuredLLMGateway;
import semsem.chatbot.schema.extractor.SchemaProvider;
import semsem.chatbot.sql.SqlSafetyValidator;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqlGeneratorNode implements NodeAction<DbChatState> {

    private final SchemaProvider schemaProvider;
    private final ChatPromptFactory promptFactory;
    private final StructuredLLMGateway llmGateway;
    private final SqlSafetyValidator sqlSafetyValidator;
    private final ObjectMapper objectMapper;

    private static final String PROMPT_NAME = "sql-generator";

    @Override
    public Map<String, Object> apply(DbChatState state) {
        return Map.of(DbChatState.Keys.SQL_GENERATION, generate(state));
    }

    private SqlGeneratorOutput generate(DbChatState state) {
        QueryAnalyzerOutput analysis = state.analysis();
        if (analysis == null) {
            log.warn("SqlGeneratorNode skipped for conversationId={}: no analysis output available",
                    state.conversationId());
            return SqlGeneratorOutput.skipped("No analysis output available");
        }

        QueryAnalyzerOutput.IntentResult intent = analysis.getIntent();
        if (!intent.isRequiresSql()) {
            log.debug("SqlGeneratorNode skipped for conversationId={}: intent '{}' does not require SQL",
                    state.conversationId(), intent.getName());
            return SqlGeneratorOutput.noSql(intent.getName());
        }

        try {
            log.debug("Generating SQL for conversationId={}, intent={}", state.conversationId(), intent.getName());
            Prompt prompt = buildPrompt(intent, analysis.getEntities(), state.userQuery());
            SqlGeneratorOutput output = llmGateway.invokeStructured(
                    FlowNames.DB_CHAT,
                    DbChatNodes.SQL_GENERATOR,
                    prompt,
                    SqlGeneratorOutput.class);
            if (!sqlSafetyValidator.isReadOnly(output.getGeneratedSql())) {
                log.warn("Generated SQL rejected by SqlSafetyValidator for conversationId={}: sql='{}'",
                        state.conversationId(), output.getGeneratedSql());
                return SqlGeneratorOutput.error("Generated SQL failed safety validation");
            }
            log.debug("Generated read-only SQL for conversationId={}: sql='{}'",
                    state.conversationId(), output.getGeneratedSql());
            return output;
        } catch (Exception e) {
            log.error("Failed to generate SQL for conversationId={}: {}", state.conversationId(), e.getMessage(), e);
            return SqlGeneratorOutput.error(e.getMessage());
        }
    }

    private Prompt buildPrompt(QueryAnalyzerOutput.IntentResult intent,
                               QueryAnalyzerOutput.ExtractedEntities entities,
                               String userQuery) throws Exception {
        return promptFactory.forPrompt(PROMPT_NAME)
                .var("intent", intent.getName())
                .var("entities", objectMapper.writeValueAsString(entities))
                .var("database_schema", schemaProvider.getDescription())
                .var("user_query", userQuery)
                .sections("enum-definitions", "query-patterns", "examples")
                .build();
    }
}