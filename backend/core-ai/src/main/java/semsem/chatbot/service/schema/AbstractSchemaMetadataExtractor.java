package semsem.chatbot.service.schema;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import semsem.chatbot.config.SchemaDataSourceProperties;
import semsem.chatbot.service.schema.model.SchemaMetadata;
import semsem.chatbot.service.schema.model.SchemaTable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract base implementation for schema metadata extraction.
 * Provides common functionality for all database types.
 */
@Slf4j
public abstract class AbstractSchemaMetadataExtractor implements SchemaMetadataExtractor {

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractSchemaMetadataExtractor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SchemaMetadata extract(SchemaDataSourceProperties properties) {
        List<String> tableNames = properties.getIncludeTables();
        if (tableNames == null || tableNames.isEmpty()) {
            tableNames = fetchAllTableNames(properties.getEffectiveSchema());
        }

        // Apply exclusions
        List<String> excludeTables = properties.getExcludeTables();
        if (excludeTables != null && !excludeTables.isEmpty()) {
            List<String> finalExclude = excludeTables;
            tableNames = tableNames.stream()
                    .filter(name -> !finalExclude.contains(name))
                    .collect(Collectors.toList());
        }

        return extract(properties, tableNames);
    }

    @Override
    public SchemaMetadata extract(SchemaDataSourceProperties properties, List<String> tableNames) {
        log.debug("Extracting schema for {} tables from {}", tableNames.size(), getSupportedType());

        List<SchemaTable> tables = tableNames.stream()
                .map(name -> extractTable(properties.getEffectiveSchema(), name, properties))
                .collect(Collectors.toList());

        return SchemaMetadata.builder()
                .databaseType(getSupportedType())
                .schemaName(properties.getEffectiveSchema())
                .tables(tables)
                .build();
    }

    @Override
    public boolean testConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            log.error("Connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Fetch all table/view names in the schema.
     */
    protected abstract List<String> fetchAllTableNames(String schemaName);

    /**
     * Extract metadata for a single table/view.
     */
    protected abstract SchemaTable extractTable(String schemaName, String tableName,
                                                  SchemaDataSourceProperties properties);
}