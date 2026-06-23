--liquibase formatted sql
--changeset jehadHamid:004-create-files-audit


CREATE TABLE IF NOT EXISTS media_manager.files_audit
(
    id            uuid NOT NULL,
    rev           INT  NOT NULL,
    revtype       SMALLINT,

    name          varchar(255),
    folder_id     BIGINT,
    shop_id       BIGINT,

    mime_type     varchar(255),
    extension     varchar(10),
    content_type  varchar(255),
    size          BIGINT,

    status        varchar(20),
    error_message varchar(255),

    visibility    varchar(20),
    bucket        varchar(255),
    cache_control varchar(255),

    created_at    TIMESTAMP,
    created_by    VARCHAR(150),
    updated_at    TIMESTAMP,
    updated_by    VARCHAR(150),

    CONSTRAINT pk_files_audit PRIMARY KEY (id, rev),
    CONSTRAINT fk_files_audit_rev
        FOREIGN KEY (rev)
            REFERENCES accounts.revinfo (rev)
);
