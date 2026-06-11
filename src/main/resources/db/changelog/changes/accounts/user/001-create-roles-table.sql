--liquibase formatted sql
--changeset lamahafiz:001-create-roles-table

CREATE SEQUENCE IF NOT EXISTS accounts.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;

CREATE TABLE IF NOT EXISTS accounts.roles (
    role_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('accounts.role_id_seq'),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50)
);