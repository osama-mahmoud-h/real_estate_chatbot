package semsem.chatbot.orchestration.dbchat.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.common.FlowNames;
import semsem.chatbot.orchestration.dbchat.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.dbchat.state.ChatMessage;
import semsem.chatbot.orchestration.dbchat.state.DbChatNodes;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;
import semsem.chatbot.prompt.builder.ChatPromptFactory;
import semsem.chatbot.llm.gateway.LLMGateway;
import semsem.chatbot.orchestration.common.ResponseFallbacks;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalResponseBuilderNode implements NodeAction<DbChatState> {

    private static final String PROMPT_NAME = "response-generator";
    private static final String DEFAULT_LANGUAGE = "English";

    private final ChatPromptFactory promptFactory;
    private final LLMGateway llmGateway;
    private final ObjectMapper objectMapper;
    private final ResponseFallbacks responseFallbacks;

    @Override
    public Map<String, Object> apply(DbChatState state) {
        String response = generate(state);
        return Map.of(
                DbChatState.Keys.RESPONSE, response,
                DbChatState.Keys.MESSAGES, List.of(ChatMessage.assistant(response)));
    }

    private String generate(DbChatState state) {
        try {
            log.debug("Building final natural-language response for conversationId={}", state.conversationId());
            String reply = llmGateway.invoke(
                    FlowNames.DB_CHAT,
                    DbChatNodes.FINAL_RESPONSE,
                    buildPrompt(state));
            if (reply == null || reply.isBlank()) {
                log.warn("FinalResponseBuilderNode received blank LLM response for conversationId={}; using deterministic fallback",
                        state.conversationId());
                return responseFallbacks.forResults(state.sqlExecution());
            }
            return reply;
        } catch (Exception e) {
            log.error("Failed to generate final response for conversationId={}: {}",
                    state.conversationId(), e.getMessage(), e);
            return responseFallbacks.forResults(state.sqlExecution());
        }
    }

    private Prompt buildPrompt(DbChatState state) throws JsonProcessingException {
        QueryAnalyzerOutput analysis = state.analysis();
        QueryAnalyzerOutput.IntentResult intent = analysis != null ? analysis.getIntent() : null;
        QueryAnalyzerOutput.ExtractedEntities entities = analysis != null ? analysis.getEntities() : null;
        SqlExecutorOutput sqlResults = state.sqlExecution();

        return promptFactory.forPrompt(PROMPT_NAME)
                .var("user_query", state.userQuery())
                .var("intent", intent != null ? intent.getName() : "GENERAL_QUESTION")
                .var("entities", entities != null ? objectMapper.writeValueAsString(entities) : "{}")
                .var("sql_results", sqlResults != null && sqlResults.getResults() != null
                        ? objectMapper.writeValueAsString(sqlResults.getResults()) : "[]")
                .var("result_count", String.valueOf(sqlResults != null ? sqlResults.getRowCount() : 0))
                .var("detected_language", DEFAULT_LANGUAGE)
                .sections("response-patterns", "examples")
                .build();
    }
}