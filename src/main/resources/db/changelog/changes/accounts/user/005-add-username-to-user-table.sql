--liquibase formatted sql
--changeset lamahafiz:005-add-username-to-user-table

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS username VARCHAR(150);

-- Make full_name nullable
ALTER TABLE users
    ALTER COLUMN full_name DROP NOT NULL;