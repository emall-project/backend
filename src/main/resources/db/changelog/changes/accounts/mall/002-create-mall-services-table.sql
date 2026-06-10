--liquibase formatted sql
--changeset lamahafiz:002-create-mall-services-table

CREATE SEQUENCE IF NOT EXISTS mall_service_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS mall_services (
    service_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('mall_service_id_seq'),
    mall_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT fk_mall_services_mall
    FOREIGN KEY (mall_id)
    REFERENCES malls(mall_id)
);