package semsem.chatbot.service.schema;

import semsem.chatbot.config.SchemaDataSourceProperties;
import semsem.chatbot.model.enums.DatabaseType;
import semsem.chatbot.service.schema.model.SchemaMetadata;

import java.util.List;

/**
 * Interface for extracting schema metadata from different database types.
 * Each database type has its own implementation.
 */
public interface SchemaMetadataExtractor {

    /**
     * Get the database type this extractor supports.
     */
    DatabaseType getSupportedType();

    /**
     * Extract schema metadata for all tables/views in the schema.
     *
     * @param properties datasource configuration
     * @return complete schema metadata
     */
    SchemaMetadata extract(SchemaDataSourceProperties properties);

    /**
     * Extract schema metadata for specific tables/views.
     *
     * @param properties datasource configuration
     * @param tableNames list of table/view names to extract
     * @return schema metadata for specified tables
     */
    SchemaMetadata extract(SchemaDataSourceProperties properties, List<String> tableNames);

    /**
     * Check if the extractor can connect to the database.
     *
     * @return true if connection is successful
     */
    boolean testConnection();
}