package semsem.chatbot.orchestration.dbchat.node;

import lombok.RequiredArgsConstructor;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.dbchat.state.DbChatState;
import semsem.chatbot.service.sql.SqlQueryRunner;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SqlExecutorNode implements NodeAction<DbChatState> {

    private final SqlQueryRunner sqlQueryRunner;

    @Override
    public Map<String, Object> apply(DbChatState state) {
        return Map.of(DbChatState.Keys.SQL_EXECUTION, sqlQueryRunner.run(state.sqlGeneration()));
    }
}