--liquibase formatted sql
--changeset lamahafiz:001-create-city-table

CREATE SEQUENCE IF NOT EXISTS city_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS cities (
    city_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('city_id_seq'),
    name VARCHAR(100) NOT NULL UNIQUE,
    base_fee NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150)
);