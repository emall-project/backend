--liquibase formatted sql
--changeset lamahafiz:007-create-malls-accounts.

CREATE TABLE IF NOT EXISTS accounts.malls_audit (
    mall_id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype SMALLINT,
    city_id BIGINT,
    name VARCHAR(255),
    description TEXT,
    capacity INTEGER,
    location VARCHAR(500),
    contact_info JSONB,
    logo_url VARCHAR(500),
    logo_uuid VARCHAR(255),
    status VARCHAR(20),
    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),
    PRIMARY KEY (mall_id, rev),
    CONSTRAINT fk_malls_audit_rev
    FOREIGN KEY (rev)
    REFERENCES accounts.revinfo(rev)
);