--liquibase formatted sql
--changeset JehadHamid:006-create-file-bindings-table

CREATE SEQUENCE IF NOT EXISTS media_manager.file_binding_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS media_manager.file_bindings
(
    id          BIGINT PRIMARY KEY DEFAULT nextval('media_manager.file_binding_seq'),
    file_id     uuid         NOT NULL,
    entity_type varchar(80)  NOT NULL,
    entity_id   varchar(120) NOT NULL,
    field_name  varchar(120) NOT NULL,
    visibility  varchar(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(150),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(150),

    CONSTRAINT fk_file_bindings_file
        FOREIGN KEY (file_id)
            REFERENCES media_manager.files (id)
            ON DELETE CASCADE,
    CONSTRAINT uk_file_binding_file_entity_field
        UNIQUE (file_id, entity_type, entity_id, field_name)
);

CREATE INDEX IF NOT EXISTS idx_file_bindings_file_visibility
    ON media_manager.file_bindings (file_id, visibility);

CREATE INDEX IF NOT EXISTS idx_file_bindings_entity
    ON media_manager.file_bindings (entity_type, entity_id, field_name);
