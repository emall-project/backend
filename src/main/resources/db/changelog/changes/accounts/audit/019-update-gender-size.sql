--liquibase formatted sql
--changeset lamahafiz:019-update-gender-size

ALTER TABLE audit.users_audit ALTER COLUMN gender TYPE VARCHAR(20);