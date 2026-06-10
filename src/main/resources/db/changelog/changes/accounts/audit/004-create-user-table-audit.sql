--liquibase formatted sql
--changeset lamahafiz:004-create-users-audit-table

CREATE TABLE IF NOT EXISTS audit.users_audit (
    user_id BIGINT NOT NULL,
    rev INT NOT NULL,
    revtype SMALLINT,

    full_name      VARCHAR(150),
    email          VARCHAR(150),
    phone_number   VARCHAR(25),
    password       VARCHAR(255),
    role_id        BIGINT,
    is_active      BOOLEAN,
    last_login_at  TIMESTAMP,

    created_at     TIMESTAMP,
    created_by     VARCHAR(150),
    updated_at     TIMESTAMP,
    updated_by     VARCHAR(150),

    CONSTRAINT pk_users_audit PRIMARY KEY (user_id, rev),
    CONSTRAINT fk_users_audit_rev
        FOREIGN KEY (rev)
        REFERENCES audit.revinfo (rev)
);
