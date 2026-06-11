--liquibase formatted sql
--changeset lamahafiz:020-add-is-protected-to-users-audit

ALTER TABLE accounts.users_audit
    ADD COLUMN IF NOT EXISTS is_protected BOOLEAN DEFAULT FALSE;
