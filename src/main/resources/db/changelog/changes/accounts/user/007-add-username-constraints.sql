--liquibase formatted sql
--changeset lamahafiz:007-add-username-constraints

ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT username_unique UNIQUE (username);