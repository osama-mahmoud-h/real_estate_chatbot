package semsem.chatbot.orchestration.dbchat.state;

public final class DbChatNodes {
    private DbChatNodes() {}

    public static final String ENTITY_EXTRACTOR = "entity_extractor";
    public static final String SQL_GENERATOR = "sql_generator";
    public static final String SQL_EXECUTOR = "sql_executor";
    public static final String FINAL_RESPONSE = "final_response";
}