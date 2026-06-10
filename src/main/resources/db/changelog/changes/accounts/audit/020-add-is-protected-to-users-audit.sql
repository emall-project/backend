--liquibase formatted sql
--changeset lamahafiz:020-add-is-protected-to-users-audit

ALTER TABLE audit.users_audit
    ADD COLUMN IF NOT EXISTS is_protected BOOLEAN NOT NULL DEFAULT FALSE;
