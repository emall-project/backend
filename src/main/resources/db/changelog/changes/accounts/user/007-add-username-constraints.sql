--liquibase formatted sql
--changeset lamahafiz:007-add-username-constraints

ALTER TABLE accounts.users
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE accounts.users
    ADD CONSTRAINT username_unique UNIQUE (username);