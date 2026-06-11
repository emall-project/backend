-- liquibase formatted sql
-- changeset jehad:002-create-job-execution-records

CREATE TABLE IF NOT EXISTS interaction.job_execution_records
(
    id             BIGSERIAL PRIMARY KEY,
    job_type       VARCHAR(100)             NOT NULL,
    entity_type    VARCHAR(100),
    entity_id      BIGINT,
    source_service VARCHAR(100),
    routing_key    VARCHAR(150),
    correlation_id VARCHAR(100),
    status         VARCHAR(30)              NOT NULL,
    started_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    finished_at    TIMESTAMP WITH TIME ZONE,
    duration_ms    BIGINT,
    error_message  TEXT,
    metadata       TEXT
);
