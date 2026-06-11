--liquibase formatted sql
--changeset lamahafiz:005-add-username-to-user-table

ALTER TABLE accounts.users
    ADD COLUMN IF NOT EXISTS username VARCHAR(150);

-- Make full_name nullable
ALTER TABLE accounts.users
    ALTER COLUMN full_name DROP NOT NULL;