package semsem.chatbot.orchestration.dbchat.state;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;
import semsem.chatbot.orchestration.dbchat.output.QueryAnalyzerOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlExecutorOutput;
import semsem.chatbot.orchestration.dbchat.output.SqlGeneratorOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DbChatState extends AgentState {

    public static final class Keys {
        private Keys() {}

        public static final String CONVERSATION_ID = "conversationId";
        public static final String USER_QUERY = "userQuery";
        public static final String MESSAGES = "messages";

        public static final String ANALYSIS = "analysis";
        public static final String SQL_GENERATION = "sqlGeneration";
        public static final String SQL_EXECUTION = "sqlExecution";
        public static final String RESPONSE = "response";
    }

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            Keys.MESSAGES, Channels.appender(ArrayList::new)
    );

    public DbChatState(Map<String, Object> initData) {
        super(initData);
    }

    public Optional<Long> conversationId() {
        return value(Keys.CONVERSATION_ID);
    }

    public String userQuery() {
        return this.<String>value(Keys.USER_QUERY).orElse("");
    }

    public List<ChatMessage> messages() {
        return this.<List<ChatMessage>>value(Keys.MESSAGES).orElseGet(List::of);
    }

    public QueryAnalyzerOutput analysis() {
        return this.<QueryAnalyzerOutput>value(Keys.ANALYSIS).orElse(null);
    }

    public SqlGeneratorOutput sqlGeneration() {
        return this.<SqlGeneratorOutput>value(Keys.SQL_GENERATION).orElse(null);
    }

    public SqlExecutorOutput sqlExecution() {
        return this.<SqlExecutorOutput>value(Keys.SQL_EXECUTION).orElse(null);
    }

    public String response() {
        return this.<String>value(Keys.RESPONSE).orElse("");
    }
}