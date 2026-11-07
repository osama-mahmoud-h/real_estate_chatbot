package semsem.chatbot.orchestration.dbchat.node;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.common.FlowNames;
import semsem.chatbot.orchestration.dbchat.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.dbchat.state.DbChatNodes;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;
import semsem.chatbot.prompt.builder.ChatPromptFactory;
import semsem.chatbot.orchestration.common.ChatHistoryMapper;
import semsem.chatbot.llm.gateway.StructuredLLMGateway;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntityExtractorNode implements NodeAction<DbChatState> {

    private static final String PROMPT_NAME = "query-analyzer";
    private static final String DEFAULT_LANGUAGE = "English";

    private final ChatPromptFactory promptFactory;
    private final StructuredLLMGateway llmGateway;
    private final ChatHistoryMapper historyMapper;

    @Override
    public Map<String, Object> apply(DbChatState state) {
        return Map.of(DbChatState.Keys.ANALYSIS, analyze(state));
    }

    private QueryAnalyzerOutput analyze(DbChatState state) {
        try {
            log.debug("Executing EntityExtractorNode for conversationId={}, userQuery='{}'",
                    state.conversationId(), state.userQuery());
            Prompt prompt = buildPrompt(state);
            QueryAnalyzerOutput output = llmGateway.invokeStructured(
                    FlowNames.DB_CHAT,
                    DbChatNodes.ENTITY_EXTRACTOR,
                    prompt,
                    QueryAnalyzerOutput.class);
            if (output != null && output.getIntent() != null) {
                log.info("Extracted intent={} (confidence={}, requiresSql={}) for conversationId={}",
                        output.getIntent().getName(), output.getIntent().getConfidence(),
                        output.getIntent().isRequiresSql(), state.conversationId());
            }
            return output;
        } catch (Exception e) {
            log.error("Failed to extract entities for conversationId={}: {}", state.conversationId(), e.getMessage(), e);
            return QueryAnalyzerOutput.fallback();
        }
    }

    private Prompt buildPrompt(DbChatState state) {
        return promptFactory.forPrompt(PROMPT_NAME)
                .var("user_query", state.userQuery())
                .var("detected_language", DEFAULT_LANGUAGE)
                .sections("intent-definitions", "entity-types", "examples")
                .history(historyMapper.toRecentMessages(state.messages()))
                .build();
    }
}
