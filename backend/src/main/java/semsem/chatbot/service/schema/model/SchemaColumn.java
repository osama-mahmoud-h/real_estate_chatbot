package semsem.chatbot.service.schema.model;

import lombok.Builder;
import lombok.Data;

/**
 * Database-agnostic representation of a column.
 */
@Data
@Builder
public class SchemaColumn {

    private String name;
    private String dataType;
    private String rawType;
    private boolean nullable;
    private String defaultValue;
    private int ordinalPosition;
    private String description;
    private boolean primaryKey;
    private String foreignTableName;
    private String foreignColumnName;

    /**
     * Check if this column is a foreign key.
     */
    public boolean isForeignKey() {
        return foreignTableName != null && !foreignTableName.isEmpty();
    }
}