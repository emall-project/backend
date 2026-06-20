--liquibase formatted sql
--changeset JehadHamid:003-create-files-table

CREATE TABLE IF NOT EXISTS media_manager.files
(
    id            uuid PRIMARY KEY,
    name          varchar(255) NOT NULL,
    folder_id     BIGINT       NOT NULL,
    shop_id       BIGINT,

    mime_type     varchar(255),
    extension     varchar(10),
    content_type  varchar(255),
    size          BIGINT,

    status        varchar(20),
    error_message varchar(255),

    visibility    varchar(20)  NOT NULL DEFAULT 'PRIVATE',
    bucket        varchar(255),
    cache_control varchar(255),

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