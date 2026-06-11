--liquibase formatted sql
--changeset lamahafiz:001-create-malls-table

CREATE SEQUENCE IF NOT EXISTS accounts.mall_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS accounts.malls (
    mall_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('accounts.mall_id_seq'),
    city_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    capacity INTEGER,
    location VARCHAR(500) NOT NULL,
    contact_info JSONB,
    logo_url VARCHAR(500),
    logo_uuid VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT fk_malls_city
    FOREIGN KEY (city_id)
    REFERENCES accounts.cities(city_id)
);
