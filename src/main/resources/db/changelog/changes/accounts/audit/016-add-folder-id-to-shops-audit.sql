-- liquibase formatted sql
-- changeset lamahafiz:016-add-folder-id-to-shops-audit

ALTER TABLE accounts.shops_audit
    ADD COLUMN folder_id BIGINT;