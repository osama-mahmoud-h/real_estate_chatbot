package semsem.chatbot.orchestration.graph.output;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Output from SQL_GENERATOR node.
 */
@Data
@Builder
public class SqlGeneratorOutput {

    private String generatedSql;
    private List<String> tables;
    private List<String> columns;
    private boolean isReadOnly;
    private String explanation;
}
