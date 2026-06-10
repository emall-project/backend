--liquibase formatted sql
--changeset lamahafiz:010-create-shops-audit

CREATE TABLE IF NOT EXISTS audit.shops_audit (
    shop_id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype SMALLINT,
    mall_id BIGINT,
    owner_user_id BIGINT,
    name VARCHAR(255),
    category VARCHAR(50),
    description TEXT,
    location VARCHAR(255),
    contact_info JSONB,
    logo_url VARCHAR(500),
    logo_uuid VARCHAR(255),
    status VARCHAR(20),
    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    PRIMARY KEY (shop_id, rev),
    CONSTRAINT fk_shops_audit_rev
    FOREIGN KEY (rev)
    REFERENCES audit.revinfo(rev)
);