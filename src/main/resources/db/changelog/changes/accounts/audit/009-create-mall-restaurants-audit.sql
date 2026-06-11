--liquibase formatted sql
--changeset lamahafiz:009-create-mall-restaurants-audit

CREATE TABLE IF NOT EXISTS accounts.mall_restaurants_audit (
    restaurant_id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype SMALLINT,
    mall_id BIGINT,
    name VARCHAR(255),
    description TEXT,
    cuisine_type VARCHAR(100),
    location_in_mall VARCHAR(255),
    contact_info JSONB,
    logo_url VARCHAR(500),
    logo_uuid VARCHAR(255),
    is_active BOOLEAN,
    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),
    PRIMARY KEY (restaurant_id, rev),
    CONSTRAINT fk_mall_restaurants_audit_rev
    FOREIGN KEY (rev)
    REFERENCES accounts.revinfo(rev)
);