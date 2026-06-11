--liquibase formatted sql
--changeset lamahafiz:002-create-user-table

CREATE SEQUENCE IF NOT EXISTS accounts.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS accounts.users (
    user_id BIGINT PRIMARY KEY DEFAULT NEXTVAL('accounts.user_id_seq'),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone_number VARCHAR(25) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT fk_users_role
            FOREIGN KEY (role_id)
            REFERENCES accounts.roles(role_id)

);
