-- liquibase formatted sql
-- changeset lamahafiz:003-add-folder-id-to-shops

ALTER TABLE shops
    ADD COLUMN folder_id BIGINT;