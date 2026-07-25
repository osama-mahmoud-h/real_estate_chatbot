package semsem.chatbot.service.schema;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import semsem.chatbot.config.SchemaDataSourceProperties;
import semsem.chatbot.model.enums.DatabaseType;
import semsem.chatbot.service.schema.model.SchemaColumn;
import semsem.chatbot.service.schema.model.SchemaTable;

import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL-specific schema metadata extractor.
 * Uses PostgreSQL system catalogs and information_schema for metadata extraction.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "chatbot.schema.datasource.postgres", name = "enabled", havingValue = "true")
public class PostgresSchemaMetadataExtractor extends AbstractSchemaMetadataExtractor {

    private static final String FETCH_TABLES_SQL = """
            SELECT table_name, table_type
            FROM information_schema.tables
            WHERE table_schema = ?
              AND table_type IN ('BASE TABLE', 'VIEW')
            ORDER BY table_name
            """;

    private static final String FETCH_TABLE_COMMENT_SQL = """
            SELECT obj_description(
                (quote_ident(?) || '.' || quote_ident(?))::regclass
            )
            """;

    private static final String FETCH_COLUMNS_SQL = """
            SELECT
                c.column_name,
                c.data_type,
                c.udt_name,
                c.is_nullable,
                c.column_default,
                c.ordinal_position,
                col_description(
                    (quote_ident(c.table_schema) || '.' || quote_ident(c.table_name))::regclass,
                    c.ordinal_position
                ) AS column_comment
            FROM information_schema.columns c
            WHERE c.table_schema = ?
              AND c.table_name = ?
            ORDER BY c.ordinal_position
            """;

    private static final String FETCH_PRIMARY_KEYS_SQL = """
            SELECT kcu.column_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
                ON tc.constraint_name = kcu.constraint_name
                AND tc.table_schema = kcu.table_schema
            WHERE tc.constraint_type = 'PRIMARY KEY'
              AND tc.table_schema = ?
              AND tc.table_name = ?
            """;

    private static final String FETCH_FOREIGN_KEYS_SQL = """
            SELECT
                kcu.column_name,
                ccu.table_name AS foreign_table_name,
                ccu.column_name AS foreign_column_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
                ON tc.constraint_name = kcu.constraint_name
                AND tc.table_schema = kcu.table_schema
            JOIN information_schema.constraint_column_usage ccu
                ON tc.constraint_name = ccu.constraint_name
                AND tc.table_schema = ccu.table_schema
            WHERE tc.constraint_type = 'FOREIGN KEY'
              AND tc.table_schema = ?
              AND tc.table_name = ?
            """;

    public PostgresSchemaMetadataExtractor(
            @Qualifier("schemaJdbcTemplate") JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        log.info("PostgresSchemaMetadataExtractor initialized (instance={})",
                System.identityHashCode(this));
    }

    @Override
    public DatabaseType getSupportedType() {
        return DatabaseType.POSTGRESQL;
    }

    @Override
    protected List<String> fetchAllTableNames(String schemaName) {
        return jdbcTemplate.query(FETCH_TABLES_SQL,
                (rs, rowNum) -> rs.getString("table_name"),
                schemaName);
    }

    @Override
    protected SchemaTable extractTable(String schemaName, String tableName,
                                        SchemaDataSourceProperties properties) {
        log.debug("Extracting metadata for table: {}.{}", schemaName, tableName);

        // Get table type and comment
        SchemaTable.ObjectType objectType = getTableType(schemaName, tableName);
        String tableComment = properties.isIncludeComments()
                ? getTableComment(schemaName, tableName)
                : null;

        // Get columns
        List<SchemaColumn> columns = extractColumns(schemaName, tableName, properties);

        return SchemaTable.builder()
                .name(tableName)
                .objectType(objectType)
                .description(tableComment)
                .columns(columns)
                .build();
    }

    private SchemaTable.ObjectType getTableType(String schemaName, String tableName) {
        String tableType = jdbcTemplate.query(FETCH_TABLES_SQL,
                (rs, rowNum) -> rs.getString("table_type"),
                schemaName).stream()
                .filter(t -> t != null)
                .findFirst()
                .orElse("BASE TABLE");

        return "VIEW".equals(tableType)
                ? SchemaTable.ObjectType.VIEW
                : SchemaTable.ObjectType.TABLE;
    }

    private String getTableComment(String schemaName, String tableName) {
        try {
            return jdbcTemplate.queryForObject(FETCH_TABLE_COMMENT_SQL, String.class,
                    schemaName, tableName);
        } catch (Exception e) {
            log.debug("No comment found for table {}.{}", schemaName, tableName);
            return null;
        }
    }

    private List<SchemaColumn> extractColumns(String schemaName, String tableName,
                                               SchemaDataSourceProperties properties) {
        // Get primary keys
        List<String> primaryKeys = jdbcTemplate.query(FETCH_PRIMARY_KEYS_SQL,
                (rs, rowNum) -> rs.getString("column_name"),
                schemaName, tableName);

        // Get foreign keys
        List<ForeignKeyInfo> foreignKeys = properties.isIncludeRelationships()
                ? fetchForeignKeys(schemaName, tableName)
                : List.of();

        // Get columns
        return jdbcTemplate.query(FETCH_COLUMNS_SQL,
                (rs, rowNum) -> {
                    String columnName = rs.getString("column_name");
                    ForeignKeyInfo fk = foreignKeys.stream()
                            .filter(f -> f.columnName.equals(columnName))
                            .findFirst()
                            .orElse(null);

                    return SchemaColumn.builder()
                            .name(columnName)
                            .dataType(rs.getString("data_type"))
                            .rawType(rs.getString("udt_name"))
                            .nullable("YES".equals(rs.getString("is_nullable")))
                            .defaultValue(rs.getString("column_default"))
                            .ordinalPosition(rs.getInt("ordinal_position"))
                            .description(properties.isIncludeComments()
                                    ? rs.getString("column_comment")
                                    : null)
                            .primaryKey(primaryKeys.contains(columnName))
                            .foreignTableName(fk != null ? fk.foreignTableName : null)
                            .foreignColumnName(fk != null ? fk.foreignColumnName : null)
                            .build();
                },
                schemaName, tableName);
    }

    private List<ForeignKeyInfo> fetchForeignKeys(String schemaName, String tableName) {
        return jdbcTemplate.query(FETCH_FOREIGN_KEYS_SQL,
                (rs, rowNum) -> new ForeignKeyInfo(
                        rs.getString("column_name"),
                        rs.getString("foreign_table_name"),
                        rs.getString("foreign_column_name")),
                schemaName, tableName);
    }

    private record ForeignKeyInfo(String columnName, String foreignTableName, String foreignColumnName) {}
}