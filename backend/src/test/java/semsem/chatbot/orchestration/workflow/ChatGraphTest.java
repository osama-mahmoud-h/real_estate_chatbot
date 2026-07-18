package semsem.chatbot.orchestration.workflow;

import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import semsem.chatbot.orchestration.graph.ChatMessage;
import semsem.chatbot.orchestration.graph.ChatState;
import semsem.chatbot.orchestration.graph.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.graph.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.graph.output.SqlGeneratorOutput;
import semsem.chatbot.orchestration.node.impl.EntityExtractorNode;
import semsem.chatbot.orchestration.node.impl.FinalResponseBuilderNode;
import semsem.chatbot.orchestration.node.impl.SqlExecutorNode;
import semsem.chatbot.orchestration.node.impl.SqlGeneratorNode;
import semsem.chatbot.service.analysis.EntityExtractionService;
import semsem.chatbot.service.response.ResponseGeneratorService;
import semsem.chatbot.service.sql.SqlExecutorService;
import semsem.chatbot.service.sql.SqlGeneratorService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatGraphTest {

    @Mock EntityExtractionService entityExtractionService;
    @Mock SqlGeneratorService sqlGeneratorService;
    @Mock SqlExecutorService sqlExecutorService;
    @Mock ResponseGeneratorService responseGeneratorService;

    @Test
    void runsPhasesInOrderAndPopulatesEachSection() throws Exception {
        QueryAnalyzerOutput analysis = QueryAnalyzerOutput.builder()
                .intent(QueryAnalyzerOutput.IntentResult.builder().name("SEARCH").confidence(0.9).build())
                .entities(QueryAnalyzerOutput.ExtractedEntities.builder().build())
                .build();
        SqlGeneratorOutput sql = SqlGeneratorOutput.builder().generatedSql("SELECT 1").isSafe(true).build();
        SqlExecutorOutput exec = SqlExecutorOutput.builder().rowCount(3).success(true).results(List.of()).build();

        when(entityExtractionService.extract(anyString(), anyString(), anyList())).thenReturn(analysis);
        when(sqlGeneratorService.generate(any(), any(), anyString())).thenReturn(sql);
        when(sqlExecutorService.execute(any())).thenReturn(exec);
        when(responseGeneratorService.generate(anyString(), any(), any(), any(), anyString()))
                .thenReturn("Here are 3 homes.");

        ChatGraph graph = new ChatGraph(
                new EntityExtractorNode(entityExtractionService),
                new SqlGeneratorNode(sqlGeneratorService),
                new SqlExecutorNode(sqlExecutorService),
                new FinalResponseBuilderNode(responseGeneratorService),
                new MemorySaver());

        ChatState result = graph.run(Map.of(
                ChatState.Keys.CONVERSATION_ID, 1L,
                ChatState.Keys.USER_QUERY, "find homes",
                ChatState.Keys.MESSAGES, List.of(ChatMessage.user("find homes"))), "thread-1");

        assertThat(result.analysis().getIntent().getName()).isEqualTo("SEARCH");
        assertThat(result.sqlGeneration().getGeneratedSql()).isEqualTo("SELECT 1");
        assertThat(result.sqlExecution().getRowCount()).isEqualTo(3);
        assertThat(result.response()).isEqualTo("Here are 3 homes.");
        assertThat(result.messages()).hasSize(2); // user + appended assistant
    }
}