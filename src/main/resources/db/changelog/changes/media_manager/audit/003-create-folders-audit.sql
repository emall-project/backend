--liquibase formatted sql
--changeset jehadHamid:003-create-folders-audit


CREATE TABLE IF NOT EXISTS media_manager.folders_audit
(
    id         BIGINT NOT NULL,
    rev        INT    NOT NULL,
    revtype    SMALLINT,

    name       varchar(50),
    parent_id  BIGINT,
    shop_id   BIGINT,

    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT pk_folders_audit PRIMARY KEY (id, rev),
    CONSTRAINT fk_folders_audit_rev
        FOREIGN KEY (rev)
            REFERENCES accounts.revinfo (rev)
);
