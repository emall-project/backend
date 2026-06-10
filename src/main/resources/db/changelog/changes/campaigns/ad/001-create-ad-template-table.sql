--liquibase formatted sql
--changeset lamahafiz:001-create-ad-template-table

CREATE SEQUENCE IF NOT EXISTS ad_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS ad_templates (
    ad_template_id  BIGINT          PRIMARY KEY DEFAULT nextval('ad_template_id_seq'),
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    position        VARCHAR(255)    NOT NULL,
    image_ratio     VARCHAR(50)     NOT NULL,
    price           NUMERIC(12, 2)  NOT NULL,
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(255)
);