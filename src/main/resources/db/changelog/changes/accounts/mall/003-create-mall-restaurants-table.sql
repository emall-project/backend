--liquibase formatted sql
--changeset lamahafiz:003-create-mall-restaurants-table

CREATE SEQUENCE IF NOT EXISTS accounts.mall_restaurant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS accounts.mall_restaurants (
    restaurant_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('accounts.mall_restaurant_id_seq'),
    mall_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cuisine_type VARCHAR(100),
    location_in_mall VARCHAR(255),
    contact_info JSONB,
    logo_url VARCHAR(500),
    logo_uuid VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT fk_mall_restaurants_mall
    FOREIGN KEY (mall_id)
    REFERENCES accounts.malls(mall_id)
);