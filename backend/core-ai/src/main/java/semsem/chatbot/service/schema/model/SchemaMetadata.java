package semsem.chatbot.service.schema.model;

import lombok.Builder;
import lombok.Data;
import semsem.chatbot.model.enums.DatabaseType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Complete schema metadata for LLM consumption.
 */
@Data
@Builder
public class SchemaMetadata {

    private DatabaseType databaseType;
    private String schemaName;

    @Builder.Default
    private List<SchemaTable> tables = new ArrayList<>();

    @Builder.Default
    private Instant fetchedAt = Instant.now();

    /**
     * Get a table by name.
     */
    public Optional<SchemaTable> getTable(String name) {
        return tables.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get all table names.
     */
    public List<String> getTableNames() {
        return tables.stream()
                .map(SchemaTable::getName)
                .toList();
    }

    /**
     * Format schema as LLM-friendly text description.
     */
    public String toLLMDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Database Schema (").append(databaseType.getDisplayName()).append(")\n");
        sb.append("-- Schema: ").append(schemaName).append("\n\n");

        for (SchemaTable table : tables) {
            sb.append("-- ").append(table.getObjectType()).append(": ").append(table.getName());
            if (table.getDescription() != null && !table.getDescription().isEmpty()) {
                sb.append(" - ").append(table.getDescription());
            }
            sb.append("\n");

            sb.append("CREATE ").append(table.getObjectType() == SchemaTable.ObjectType.VIEW ? "VIEW" : "TABLE")
              .append(" ").append(table.getName()).append(" (\n");

            List<SchemaColumn> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SchemaColumn col = columns.get(i);
                sb.append("    ").append(col.getName()).append(" ").append(col.getDataType());

                if (col.isPrimaryKey()) {
                    sb.append(" PRIMARY KEY");
                }
                if (!col.isNullable()) {
                    sb.append(" NOT NULL");
                }
                if (col.getDefaultValue() != null) {
                    sb.append(" DEFAULT ").append(col.getDefaultValue());
                }

                if (col.getDescription() != null && !col.getDescription().isEmpty()) {
                    sb.append(" -- ").append(col.getDescription());
                }

                if (i < columns.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }

            List<SchemaColumn> fkColumns = table.getForeignKeyColumns();
            if (!fkColumns.isEmpty()) {
                for (SchemaColumn fk : fkColumns) {
                    sb.append("    -- FK: ").append(fk.getName())
                      .append(" -> ").append(fk.getForeignTableName())
                      .append("(").append(fk.getForeignColumnName()).append(")\n");
                }
            }

            sb.append(");\n\n");
        }

        return sb.toString();
    }
}