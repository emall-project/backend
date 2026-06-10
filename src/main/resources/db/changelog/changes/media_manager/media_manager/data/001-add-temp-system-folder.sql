--liquibase formatted sql
--changeset JehadHamid:001-add-temp-system-folder

INSERT INTO media_manager.folders(id, name, scope, managed_by, created_at, created_by)
VALUES (nextval('media_manager.folder_id_seq'), 'TEMP_FOLDER', 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM');