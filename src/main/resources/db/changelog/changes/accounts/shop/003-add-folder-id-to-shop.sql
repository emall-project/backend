-- liquibase formatted sql
-- changeset lamahafiz:003-add-folder-id-to-shops

ALTER TABLE accounts.shops
    ADD COLUMN folder_id BIGINT;