--liquibase formatted sql
--changeset lamahafiz:008-create-mall-services-audit

CREATE TABLE IF NOT EXISTS accounts.mall_services_audit (
    service_id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype SMALLINT,
    mall_id BIGINT,
    name VARCHAR(255),
    description TEXT,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),
    PRIMARY KEY (service_id, rev),
    CONSTRAINT fk_mall_services_audit_rev
    FOREIGN KEY (rev)
    REFERENCES accounts.revinfo(rev)
);