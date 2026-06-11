--liquibase formatted sql
--changeset lamahafiz:019-update-gender-size

ALTER TABLE accounts.users_audit ALTER COLUMN gender TYPE VARCHAR(20);