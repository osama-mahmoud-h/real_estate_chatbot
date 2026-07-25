package semsem.chatbot.orchestration.dbchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import semsem.chatbot.orchestration.dbchat.state.ChatMessage;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;
import semsem.chatbot.orchestration.dbchat.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlGeneratorOutput;
import semsem.chatbot.orchestration.dbchat.node.EntityExtractorNode;
import semsem.chatbot.orchestration.dbchat.node.FinalResponseBuilderNode;
import semsem.chatbot.orchestration.dbchat.node.SqlExecutorNode;
import semsem.chatbot.orchestration.dbchat.node.SqlGeneratorNode;
import semsem.chatbot.prompt.builder.ChatPromptFactory;
import semsem.chatbot.service.conversation.ChatHistoryMapper;
import semsem.chatbot.service.llm.gateway.LLMGateway;
import semsem.chatbot.service.llm.gateway.StructuredLLMGateway;
import semsem.chatbot.service.response.ResponseFallbacks;
import semsem.chatbot.service.schema.SchemaProvider;
import semsem.chatbot.service.sql.SqlQueryRunner;
import semsem.chatbot.service.sql.SqlSafetyValidator;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DbChatGraphTest {

    // Every node orchestrates single-responsibility collaborators directly (no service layer).
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) ChatPromptFactory promptFactory;
    @Mock StructuredLLMGateway structuredLlmGateway;
    @Mock LLMGateway llmGateway;
    @Mock ChatHistoryMapper historyMapper;
    @Mock SchemaProvider schemaProvider;
    @Mock SqlSafetyValidator sqlSafetyValidator;
    @Mock SqlQueryRunner sqlQueryRunner;
    @Mock ResponseFallbacks responseFallbacks;

    @Test
    void runsPhasesInOrderAndPopulatesEachSection() throws Exception {
        QueryAnalyzerOutput analysis = QueryAnalyzerOutput.builder()
                .intent(QueryAnalyzerOutput.IntentResult.builder()
                        .name("SEARCH").confidence(0.9).requiresSql(true).build())
                .entities(QueryAnalyzerOutput.ExtractedEntities.builder().build())
                .build();
        SqlGeneratorOutput sql = SqlGeneratorOutput.builder().generatedSql("SELECT 1").isSafe(true).build();
        SqlExecutorOutput exec = SqlExecutorOutput.builder().rowCount(3).success(true).results(List.of()).build();

        when(structuredLlmGateway.invokeStructured(any(), eq(QueryAnalyzerOutput.class))).thenReturn(analysis);
        when(structuredLlmGateway.invokeStructured(any(), eq(SqlGeneratorOutput.class))).thenReturn(sql);
        when(sqlSafetyValidator.isReadOnly(anyString())).thenReturn(true);
        when(sqlQueryRunner.run(any())).thenReturn(exec);
        when(llmGateway.invoke(any())).thenReturn("Here are 3 homes.");

        DbChatGraph graph = new DbChatGraph(
                new EntityExtractorNode(promptFactory, structuredLlmGateway, historyMapper),
                new SqlGeneratorNode(schemaProvider, promptFactory, structuredLlmGateway,
                        sqlSafetyValidator, new ObjectMapper()),
                new SqlExecutorNode(sqlQueryRunner),
                new FinalResponseBuilderNode(promptFactory, llmGateway, new ObjectMapper(), responseFallbacks),
                new MemorySaver());

        DbChatState result = graph.run(Map.of(
                DbChatState.Keys.CONVERSATION_ID, 1L,
                DbChatState.Keys.USER_QUERY, "find homes",
                DbChatState.Keys.MESSAGES, List.of(ChatMessage.user("find homes"))), "thread-1");

        assertThat(result.analysis().getIntent().getName()).isEqualTo("SEARCH");
        assertThat(result.sqlGeneration().getGeneratedSql()).isEqualTo("SELECT 1");
        assertThat(result.sqlExecution().getRowCount()).isEqualTo(3);
        assertThat(result.response()).isEqualTo("Here are 3 homes.");
        assertThat(result.messages()).hasSize(2); // user + appended assistant
    }
}