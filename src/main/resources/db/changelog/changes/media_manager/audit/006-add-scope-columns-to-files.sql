--liquibase formatted sql
--changeset JehadHamid:005-add-scope-columns-to-files

ALTER TABLE media_manager.files_audit
    ADD COLUMN IF NOT EXISTS scope VARCHAR(20),
    ADD COLUMN IF NOT EXISTS managed_by VARCHAR(50);
