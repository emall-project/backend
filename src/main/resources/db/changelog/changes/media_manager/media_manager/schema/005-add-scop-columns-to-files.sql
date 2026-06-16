--liquibase formatted sql
--changeset JehadHamid:005-add-scope-columns-to-files

ALTER TABLE media_manager.files
    ADD COLUMN IF NOT EXISTS scope VARCHAR(20),
    ADD COLUMN IF NOT EXISTS managed_by VARCHAR(50);

UPDATE media_manager.files
SET scope = CASE
                WHEN store_id IS NULL THEN 'SYSTEM'
                ELSE 'SHOP'
    END;

UPDATE media_manager.files
SET managed_by = CASE
                     WHEN store_id IS NULL THEN 'ADMIN'
                     ELSE 'SHOP'
    END;

ALTER TABLE media_manager.files
    ALTER COLUMN scope SET NOT NULL;

ALTER TABLE media_manager.files
    ALTER COLUMN managed_by SET NOT NULL;