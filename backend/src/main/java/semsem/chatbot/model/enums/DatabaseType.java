package semsem.chatbot.model.enums;

/**
 * Supported database types for schema extraction.
 */
public enum DatabaseType {

    POSTGRESQL("postgresql", "PostgreSQL", "org.postgresql.Driver", "public"),
    MYSQL("mysql", "MySQL", "com.mysql.cj.jdbc.Driver", null),
    MARIADB("mariadb", "MariaDB", "org.mariadb.jdbc.Driver", null),
    ORACLE("oracle", "Oracle", "oracle.jdbc.OracleDriver", null),
    SQLSERVER("sqlserver", "SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "dbo"),
    H2("h2", "H2", "org.h2.Driver", "PUBLIC");

    private final String key;
    private final String displayName;
    private final String defaultDriver;
    private final String defaultSchema;

    DatabaseType(String key, String displayName, String defaultDriver, String defaultSchema) {
        this.key = key;
        this.displayName = displayName;
        this.defaultDriver = defaultDriver;
        this.defaultSchema = defaultSchema;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDriver() {
        return defaultDriver;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public static DatabaseType fromKey(String key) {
        for (DatabaseType type : values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown database type: " + key);
    }
}