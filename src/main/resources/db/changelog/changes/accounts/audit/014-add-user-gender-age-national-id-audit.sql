--liquibase formatted sql
--changeset lamahafiz:014-add-user-gender-age-national-id-audit

ALTER TABLE accounts.users_audit
    ADD COLUMN IF NOT EXISTS gender VARCHAR(10);

ALTER TABLE accounts.users_audit
    ADD COLUMN IF NOT EXISTS age INTEGER;

ALTER TABLE accounts.users_audit
    ADD COLUMN IF NOT EXISTS national_id_number VARCHAR(20);
