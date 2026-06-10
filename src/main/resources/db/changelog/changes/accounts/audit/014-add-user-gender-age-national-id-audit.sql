--liquibase formatted sql
--changeset lamahafiz:014-add-user-gender-age-national-id-audit

ALTER TABLE audit.users_audit
    ADD COLUMN IF NOT EXISTS gender VARCHAR(10);

ALTER TABLE audit.users_audit
    ADD COLUMN IF NOT EXISTS age INTEGER;

ALTER TABLE audit.users_audit
    ADD COLUMN IF NOT EXISTS national_id_number VARCHAR(20);
