--liquibase formatted sql
--changeset jehadHamid:003-create-folders-audit


CREATE TABLE IF NOT EXISTS audit.folders_audit
(
    id         BIGINT NOT NULL,
    rev        INT    NOT NULL,
    revtype    SMALLINT,

    name       varchar(50),
    parent_id  BIGINT,
    store_id   BIGINT,

    created_at TIMESTAMP,
    created_by VARCHAR(150),
    updated_at TIMESTAMP,
    updated_by VARCHAR(150),

    CONSTRAINT pk_folders_audit PRIMARY KEY (id, rev),
    CONSTRAINT fk_folders_audit_rev
        FOREIGN KEY (rev)
            REFERENCES audit.revinfo (rev)
);
