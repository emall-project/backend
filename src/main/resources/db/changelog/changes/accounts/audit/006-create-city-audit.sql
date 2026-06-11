--liquibase formatted sql
--changeset lamahafiz:006-create-city-audit

CREATE TABLE IF NOT EXISTS accounts.cities_audit (
    rev INT NOT NULL,
    revtype SMALLINT,
    city_id BIGINT NOT NULL,
    name VARCHAR(100),
    base_fee NUMERIC(10, 2),
    is_active BOOLEAN,
    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    PRIMARY KEY (city_id, rev),
    CONSTRAINT fk_cities_audit_rev
        FOREIGN KEY (rev)
        REFERENCES accounts.revinfo (rev)
);