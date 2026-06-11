--liquibase formatted sql
--changeset lamahafiz:012-add-is-protected-to-users

ALTER TABLE accounts.users
    ADD COLUMN IF NOT EXISTS is_protected BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE accounts.users
SET is_protected = TRUE
WHERE username = 'admin';