--liquibase formatted sql
--changeset JehadHamid:004-add-scope-columns-to-folder

ALTER TABLE media_manager.folders
    ADD COLUMN IF NOT EXISTS scope      VARCHAR(20),
    ADD COLUMN IF NOT EXISTS managed_by VARCHAR(50);

UPDATE media_manager.folders
SET scope = CASE
                WHEN store_id IS NULL THEN 'SYSTEM'
                ELSE 'STORE'
    END;

UPDATE media_manager.folders
SET managed_by = CASE
                     WHEN store_id IS NULL THEN 'ADMIN'
                     ELSE 'STORE'
    END;

ALTER TABLE media_manager.folders
    ALTER COLUMN scope SET NOT NULL;

ALTER TABLE media_manager.folders
    ALTER COLUMN managed_by SET NOT NULL;