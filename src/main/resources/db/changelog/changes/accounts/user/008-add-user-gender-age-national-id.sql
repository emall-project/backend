--liquibase formatted sql
--changeset lamahafiz:008-add-user-gender-age-national-id

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS gender VARCHAR(10);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS age INTEGER;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS national_id_number VARCHAR(20);

ALTER TABLE users
    ADD CONSTRAINT uk_users_national_id UNIQUE (national_id_number);
