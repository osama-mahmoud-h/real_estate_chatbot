package semsem.chatbot.service.sql;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SqlSafetyValidator {

    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "INSERT", "UPDATE", "DELETE", "DROP", "TRUNCATE", "ALTER", "CREATE", "GRANT", "REVOKE");

    public boolean isReadOnly(String sql) {
        if (sql == null) {
            return false;
        }
        String upper = sql.toUpperCase();
        return FORBIDDEN_KEYWORDS.stream().noneMatch(upper::contains);
    }
}