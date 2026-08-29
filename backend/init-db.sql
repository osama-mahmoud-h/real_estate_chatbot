-- Runs ONLY on first initialization, when the postgres data directory is empty.
-- An existing volume makes the entrypoint skip this file entirely.
--
-- Connected as POSTGRES_USER to POSTGRES_DB (the chatbot's own database, already
-- created by the entrypoint). psql runs this with ON_ERROR_STOP=1.

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS dblink;

-- Business database the chatbot introspects and queries.
-- Name must match SCHEMA_POSTGRES_URL in backend/.env.
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'real_estate_db') THEN
            PERFORM dblink_exec('dbname=' || current_database(), 'CREATE DATABASE real_estate_db');
        END IF;
    END;
$$;

\connect real_estate_db

\i /docker-entrypoint-initdb.d/seed/v1_real_estate_schema_ddl.sql
\i /docker-entrypoint-initdb.d/seed/v1_real_estate_schema_dml.sql
\i /docker-entrypoint-initdb.d/seed/v2_schema_comments.sql