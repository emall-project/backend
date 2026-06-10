--liquibase formatted sql
--changeset lamahafiz:005-add-username-to-user-audit-table

ALTER TABLE audit.users_audit
    ADD COLUMN IF NOT EXISTS username VARCHAR(150);