package semsem.chatbot.service.schema.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Database-agnostic representation of a table or view.
 */
@Data
@Builder
public class SchemaTable {

    public enum ObjectType {
        TABLE,
        VIEW
    }

    private String name;
    private ObjectType objectType;
    private String description;

    @Builder.Default
    private List<SchemaColumn> columns = new ArrayList<>();

    /**
     * Get primary key columns.
     */
    public List<SchemaColumn> getPrimaryKeyColumns() {
        return columns.stream()
                .filter(SchemaColumn::isPrimaryKey)
                .toList();
    }

    /**
     * Get foreign key columns.
     */
    public List<SchemaColumn> getForeignKeyColumns() {
        return columns.stream()
                .filter(SchemaColumn::isForeignKey)
                .toList();
    }
}