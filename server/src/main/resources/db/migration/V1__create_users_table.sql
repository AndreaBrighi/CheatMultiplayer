-- Flyway migration: create users table
-- Compatible with Postgres and H2 (simple types)

CREATE TABLE IF NOT EXISTS USERS (
    ID BIGSERIAL PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    CREATED_AT TIMESTAMP
);

