-- Runs ONLY on first initialization, when the postgres data directory is empty.
-- An existing volume makes the entrypoint skip this file entirely.
--
-- Connected as POSTGRES_USER to POSTGRES_DB (the chatbot's own primary database,
-- whose schema and seed data are managed by Liquibase XML changelogs at startup).

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS dblink;

-- External business database that the chatbot introspects and queries via Text-to-SQL.
-- Name must match SCHEMA_POSTGRES_URL in backend/.env.
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'real_estate_db') THEN
            PERFORM dblink_exec('dbname=' || current_database(), 'CREATE DATABASE real_estate_db');
        END IF;
    END;
$$;

\connect real_estate_db

\i /docker-entrypoint-initdb.d/external/v1_real_estate_schema_ddl.sql
\i /docker-entrypoint-initdb.d/external/v1_real_estate_schema_dml.sql
\i /docker-entrypoint-initdb.d/external/v2_schema_comments.sql
