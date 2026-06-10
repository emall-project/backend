--liquibase formatted sql
--changeset lamahafiz:001-create-shops-table

CREATE SEQUENCE IF NOT EXISTS shop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS shops (
    shop_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('shop_id_seq'),
    mall_id BIGINT NOT NULL,
    owner_user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    location VARCHAR(255) NOT NULL,
    contact_info JSONB,
    logo_url VARCHAR(500),
    logo_uuid VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT fk_shops_mall
    FOREIGN KEY (mall_id)
    REFERENCES malls (mall_id),

    CONSTRAINT fk_shops_owner
    FOREIGN KEY (owner_user_id)
    REFERENCES users (user_id)
);