--liquibase formatted sql
--changeset JehadHamid:003-create-files-table

CREATE TABLE IF NOT EXISTS media_manager.files
(
    id            uuid PRIMARY KEY,
    name          varchar(255) NOT NULL,
    folder_id     BIGINT       NOT NULL,
    mime_type     varchar(20),
    extension     varchar(10),
    size          BIGINT,
    status        varchar(20),
    error_message varchar(255),
    store_id      BIGINT,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(150),
    updated_at    TIMESTAMP,
    updated_by    VARCHAR(150),

    CONSTRAINT unique_name_and_folder_id_extension
        UNIQUE (name, folder_id, extension),

    CONSTRAINT fk_file_folder
        FOREIGN KEY (folder_id)
            REFERENCES media_manager.folders (id)
);