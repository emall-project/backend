--liquibase formatted sql
--changeset JehadHamid:004-add-scope-columns-to-folder

ALTER TABLE media_manager.folders_audit
    ADD COLUMN IF NOT EXISTS scope      VARCHAR(20),
    ADD COLUMN IF NOT EXISTS managed_by VARCHAR(50);
