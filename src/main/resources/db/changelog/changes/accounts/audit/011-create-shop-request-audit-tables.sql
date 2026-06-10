--liquibase formatted sql
--changeset lamahafiz:011-create-shop-request-audit-tables

CREATE TABLE IF NOT EXISTS audit.shop_owner_requests_audit (
    id               BIGINT,
    rev              INTEGER NOT NULL,
    revtype          SMALLINT,
    full_name        VARCHAR(255),
    username         VARCHAR(150),
    email            VARCHAR(150),
    phone_number     VARCHAR(25),
    password         VARCHAR(255),
    gender           VARCHAR(10),
    age              INTEGER,
    national_id_number VARCHAR(20),
    profile_picture_uuid UUID,
    status           VARCHAR(20),
    rejection_reason TEXT,
    created_user_id  BIGINT,
    created_at       TIMESTAMP,
    created_by       VARCHAR(150),
    updated_at       TIMESTAMP,
    updated_by       VARCHAR(150),

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_shops_audit_rev
    FOREIGN KEY (rev)
    REFERENCES audit.revinfo(rev)
);

CREATE TABLE IF NOT EXISTS audit.shop_requests_audit (
    id                    BIGINT,
    rev                   INTEGER NOT NULL,
    revtype               SMALLINT,
    shop_owner_request_id BIGINT,
    mall_id               BIGINT,
    name                  VARCHAR(255),
    category              VARCHAR(50),
    description           TEXT,
    location              VARCHAR(255),
    contact_info          JSONB,
    logo_uuid             UUID,
    license_image_uuid    UUID,
    shop_photos_uuids     JSONB,
    status                VARCHAR(20),
    rejection_reason      TEXT,
    created_shop_id       BIGINT,
    created_at            TIMESTAMP,
    created_by            VARCHAR(150),
    updated_at            TIMESTAMP,
    updated_by            VARCHAR(150),

    PRIMARY KEY (id, rev),
    CONSTRAINT fk_shops_audit_rev
    FOREIGN KEY (rev)
    REFERENCES audit.revinfo(rev)
);