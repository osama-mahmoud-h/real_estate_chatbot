-- ============================================
-- LLM SCHEMA METADATA VIEW
-- Creates a view that exposes schema metadata with comments
-- for LLM-based SQL generation
-- ============================================

CREATE OR REPLACE VIEW llm_schema_metadata AS
SELECT
    c.table_name,
    CASE
        WHEN t.table_type = 'VIEW' THEN 'view'
        ELSE 'table'
    END AS object_type,
    obj_description(
        (quote_ident(c.table_schema) || '.' || quote_ident(c.table_name))::regclass
    ) AS table_description,
    c.column_name,
    c.data_type,
    c.udt_name AS raw_type,
    c.is_nullable,
    c.column_default,
    c.ordinal_position,
    col_description(
        (quote_ident(c.table_schema) || '.' || quote_ident(c.table_name))::regclass,
        c.ordinal_position
    ) AS column_description,
    -- Check if column is primary key
    CASE WHEN pk.column_name IS NOT NULL THEN true ELSE false END AS is_primary_key,
    -- Foreign key info
    fk.foreign_table_name,
    fk.foreign_column_name
FROM information_schema.columns c
JOIN information_schema.tables t
    ON c.table_name = t.table_name
    AND c.table_schema = t.table_schema
-- Primary key detection
LEFT JOIN (
    SELECT kcu.table_schema, kcu.table_name, kcu.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
    WHERE tc.constraint_type = 'PRIMARY KEY'
) pk ON c.table_schema = pk.table_schema
    AND c.table_name = pk.table_name
    AND c.column_name = pk.column_name
-- Foreign key detection
LEFT JOIN (
    SELECT
        kcu.table_schema,
        kcu.table_name,
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
) fk ON c.table_schema = fk.table_schema
    AND c.table_name = fk.table_name
    AND c.column_name = fk.column_name
WHERE c.table_schema = 'public'
ORDER BY c.table_name, c.ordinal_position;

-- Grant read access (adjust role as needed)
-- GRANT SELECT ON llm_schema_metadata TO your_app_role;