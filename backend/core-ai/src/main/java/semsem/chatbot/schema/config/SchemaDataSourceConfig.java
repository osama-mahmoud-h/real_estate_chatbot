package semsem.chatbot.schema.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Configuration for the external schema datasource.
 * Supports multiple database types through abstract properties.
 * Thread-safe: HikariCP and JdbcTemplate are designed for concurrent access.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class SchemaDataSourceConfig {

    @Autowired(required = false)
    private PostgresSchemaDataSourceProperties postgresProperties;

    // Add other database properties here as needed (e.g., MySqlSchemaDataSourceProperties)

    @Bean
    @Primary
    public DataSource primaryDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * Get the active schema datasource properties.
     * Returns the first enabled configuration.
     * Extend this method when adding new database types.
     */
    public Optional<SchemaDataSourceProperties> getActiveProperties() {
        if (postgresProperties != null && postgresProperties.isEnabled()) {
            return Optional.of(postgresProperties);
        }
        // Add checks for other database types here
        return Optional.empty();
    }

    @Bean(name = "schemaDataSource")
    @ConditionalOnProperty(
            prefix = "chatbot.schema.datasource",
            name = "active",
            havingValue = "true"
    )
    public DataSource schemaDataSource() {
        SchemaDataSourceProperties props = getActiveProperties()
                .orElseThrow(() -> new IllegalStateException(
                        "No schema datasource is enabled. Enable one in application.yml"));

        log.info("Creating singleton schemaDataSource: type={}, url={}, poolSize={}",
                props.getType().getDisplayName(),
                props.getUrl(),
                props.getPoolSize());

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setDriverClassName(props.getDriverClassName());
        config.setMaximumPoolSize(props.getPoolSize());
        config.setMinimumIdle(1);
        config.setPoolName("schema-pool-" + props.getType().getKey());
        config.setReadOnly(true);
        config.setConnectionTestQuery("SELECT 1");
        config.setInitializationFailTimeout(-1);

        HikariDataSource dataSource = new HikariDataSource(config);
        try (var conn = dataSource.getConnection()) {
            log.info("External schemaDataSource connected successfully [type={}, url={}]",
                    props.getType().getDisplayName(), props.getUrl());
        } catch (Exception ex) {
            log.error("\n================================================================================\n" +
                      "[EXTERNAL_DB_CONNECTION_ERROR] Could not connect to EXTERNAL target database at '{}' (user='{}').\n" +
                      "Application will continue starting up and fall back to static SQL schema (classpath:db/external/v1_real_estate_schema_ddl.sql).\n" +
                      "Please check SCHEMA_POSTGRES_URL, SCHEMA_POSTGRES_USERNAME, and SCHEMA_POSTGRES_PASSWORD in backend/.env.\n" +
                      "Root cause: {}\n" +
                      "================================================================================",
                    props.getUrl(), props.getUsername(), ex.getMessage());
        }
        return dataSource;
    }

    @Bean(name = "schemaJdbcTemplate")
    @ConditionalOnProperty(
            prefix = "chatbot.schema.datasource",
            name = "active",
            havingValue = "true"
    )
    public JdbcTemplate schemaJdbcTemplate(@Qualifier("schemaDataSource") DataSource schemaDataSource) {
        JdbcTemplate template = new JdbcTemplate(schemaDataSource);

        log.info("Singleton schemaJdbcTemplate created successfully (instance={})",
                System.identityHashCode(template));

        return template;
    }
}