package semsem.chatbot.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import semsem.chatbot.model.enums.DatabaseType;

/**
 * PostgreSQL-specific schema datasource configuration.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "chatbot.schema.datasource.postgres")
public class PostgresSchemaDataSourceProperties extends SchemaDataSourceProperties {

    private static final DatabaseType TYPE = DatabaseType.POSTGRESQL;

    @Override
    public DatabaseType getType() {
        return TYPE;
    }

    @Override
    public String getDriverClassName() {
        return TYPE.getDefaultDriver();
    }
}