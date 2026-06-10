-- liquibase formatted sql
-- changeset jehad:003-create-model-invocation-records


CREATE TABLE model_invocation_records
(
    id               BIGSERIAL PRIMARY KEY,
    model_name       VARCHAR(100) NOT NULL,
    provider         VARCHAR(100) NOT NULL,
    operation_name   VARCHAR(100) NOT NULL,
    job_execution_id BIGINT,
    http_status      INT,
    status           VARCHAR(30)  NOT NULL,
    started_at       TIMESTAMP    NOT NULL,
    finished_at      TIMESTAMP,
    duration_ms      BIGINT,
    error_message    TEXT,
    metadata         TEXT
);