package semsem.chatbot.config;

import lombok.Data;
import semsem.chatbot.model.enums.DatabaseType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base configuration for schema datasource.
 * Extend this class for specific database type implementations.
 */
@Data
public abstract class SchemaDataSourceProperties {

    /**
     * Whether to use dynamic schema loading from database.
     * If false, falls back to static file loading.
     */
    private boolean enabled = false;

    /**
     * JDBC URL for the external database.
     */
    private String url;

    /**
     * Database username.
     */
    private String username;

    /**
     * Database password.
     */
    private String password;

    /**
     * Database schema to query.
     */
    private String schema;

    /**
     * List of table/view names to include in schema description.
     * If empty, all tables/views in the schema are included.
     */
    private List<String> includeTables = new ArrayList<>();

    /**
     * List of table/view names to exclude from schema description.
     */
    private List<String> excludeTables = new ArrayList<>();

    /**
     * Whether to include table/column comments in schema description.
     */
    private boolean includeComments = true;

    /**
     * Whether to include foreign key relationships in schema description.
     */
    private boolean includeRelationships = true;

    /**
     * Cache TTL in seconds for schema metadata.
     * Set to 0 to disable caching.
     */
    private long cacheTtlSeconds = 3600;

    /**
     * Connection pool size for schema queries.
     */
    private int poolSize = 2;

    /**
     * Get the database type for this configuration.
     */
    public abstract DatabaseType getType();

    /**
     * Get the JDBC driver class name.
     */
    public abstract String getDriverClassName();

    /**
     * Get the effective schema name (with default fallback).
     */
    public String getEffectiveSchema() {
        return schema != null ? schema : getType().getDefaultSchema();
    }
}
