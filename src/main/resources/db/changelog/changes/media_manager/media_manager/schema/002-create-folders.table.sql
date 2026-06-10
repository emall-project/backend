--liquibase formatted sql
--changeset JehadHamid:002-create-folders-table

CREATE SEQUENCE IF NOT EXISTS media_manager.folder_id_seq
    START WITH 100
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;

CREATE TABLE IF NOT EXISTS media_manager.folders
(
    id         BIGINT PRIMARY KEY DEFAULT NEXTVAL('media_manager.folder_id_seq'),
    name       varchar(50),
    parent_id  BIGINT,
    store_id   BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT unique_name_and_parent_id
        UNIQUE (name, parent_id),

    CONSTRAINT fk_folder_parent_id
        FOREIGN KEY (parent_id)
            REFERENCES media_manager.folders (id)

);