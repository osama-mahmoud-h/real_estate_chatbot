package semsem.chatbot.service.response;

import org.springframework.stereotype.Component;
import semsem.chatbot.orchestration.dbchat.output.SqlExecutorOutput;

@Component
public class ResponseFallbacks {

    public String forResults(SqlExecutorOutput sqlResults) {
        if (sqlResults == null || !sqlResults.isSuccess()) {
            return "I apologize, but I encountered an issue processing your request. Please try again.";
        }
        if (sqlResults.getRowCount() == 0) {
            return "I couldn't find any results matching your criteria. Would you like to try a different search?";
        }
        return String.format("I found %d result(s) for your query.", sqlResults.getRowCount());
    }
}