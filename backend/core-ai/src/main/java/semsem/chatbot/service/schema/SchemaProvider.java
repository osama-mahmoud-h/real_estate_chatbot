package semsem.chatbot.service.schema;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import semsem.chatbot.config.SchemaDataSourceConfig;
import semsem.chatbot.config.SchemaDataSourceProperties;
import semsem.chatbot.service.schema.model.SchemaMetadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Component
public class SchemaProvider {

    private final ResourceLoader resourceLoader;

    @Autowired(required = false)
    private SchemaMetadataExtractor schemaExtractor;

    @Autowired(required = false)
    private SchemaDataSourceConfig schemaDataSourceConfig;

    @Value("${chatbot.schema.path:classpath:db/v1_real_estate_schema_ddl.sql}")
    private String schemaPath;

    private String cachedSchema;
    private SchemaMetadata cachedSchemaMetadata;
    private Instant cacheExpiry;

    public SchemaProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        loadSchema();
    }

    public String getDescription() {
        refreshSchemaIfNeeded();
        return cachedSchema;
    }

    private void loadSchema() {
        if (isDynamicSchemaEnabled()) {
            loadSchemaFromDatabase();
        } else {
            loadSchemaFromFile();
        }
    }

    private boolean isDynamicSchemaEnabled() {
        return schemaExtractor != null && schemaDataSourceConfig != null
                && schemaDataSourceConfig.getActiveProperties().isPresent();
    }

    private void loadSchemaFromDatabase() {
        try {
            SchemaDataSourceProperties props = schemaDataSourceConfig.getActiveProperties()
                    .orElseThrow(() -> new IllegalStateException("No active schema datasource"));

            log.info("Loading schema dynamically from database: type={}, schema={}",
                    props.getType().getDisplayName(),
                    props.getEffectiveSchema());

            cachedSchemaMetadata = schemaExtractor.extract(props);
            cachedSchema = cachedSchemaMetadata.toLLMDescription();
            long ttl = props.getCacheTtlSeconds();
            cacheExpiry = ttl > 0 ? Instant.now().plusSeconds(ttl) : null;

            log.info("Loaded {} tables from database, cache TTL: {}s",
                    cachedSchemaMetadata.getTables().size(),
                    ttl);

        } catch (Exception e) {
            log.error("Failed to load schema from database, falling back to file: {}",
                    e.getMessage());
            loadSchemaFromFile();
        }
    }

    private void loadSchemaFromFile() {
        try {
            Resource resource = resourceLoader.getResource(schemaPath);
            cachedSchema = resource.getContentAsString(StandardCharsets.UTF_8);
            cachedSchemaMetadata = null;
            cacheExpiry = null;
            log.info("Loaded database schema from file: {}", schemaPath);
        } catch (IOException e) {
            log.error("Failed to load schema from {}: {}", schemaPath, e.getMessage());
            cachedSchema = "Schema not available";
        }
    }

    private void refreshSchemaIfNeeded() {
        if (isDynamicSchemaEnabled() && cacheExpiry != null && Instant.now().isAfter(cacheExpiry)) {
            log.debug("Schema cache expired, refreshing...");
            loadSchemaFromDatabase();
        }
    }
}