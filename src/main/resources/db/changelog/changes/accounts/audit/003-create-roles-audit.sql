--liquibase formatted sql
--changeset lamahafiz:003-create-roles-audit

CREATE TABLE IF NOT EXISTS accounts.roles_audit (
    role_id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype SMALLINT,
    code VARCHAR(50),
    name VARCHAR(100),
    created_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50),
    PRIMARY KEY (role_id, rev),
    CONSTRAINT fk_roles_audit_rev
        FOREIGN KEY (rev)
        REFERENCES accounts.revinfo (rev)
);
