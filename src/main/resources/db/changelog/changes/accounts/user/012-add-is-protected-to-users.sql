--liquibase formatted sql
--changeset lamahafiz:012-add-is-protected-to-users

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_protected BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users
SET is_protected = TRUE
WHERE username = 'admin';