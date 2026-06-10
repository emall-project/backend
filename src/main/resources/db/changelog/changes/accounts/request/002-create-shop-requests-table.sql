--liquibase formatted sql
--changeset lamahafiz:002-create-shop-requests-table

CREATE SEQUENCE IF NOT EXISTS shop_request_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS shop_requests (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('shop_request_id_seq'),
    shop_owner_request_id BIGINT NOT NULL UNIQUE,
    mall_id               BIGINT NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    category              VARCHAR(50)  NOT NULL,
    description           TEXT,
    location              VARCHAR(255) NOT NULL,
    contact_info          JSONB,
    logo_uuid             UUID,
    license_image_uuid    UUID         NOT NULL,
    shop_photos_uuids     JSONB        NOT NULL DEFAULT '[]',
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    rejection_reason      TEXT,
    created_shop_id       BIGINT,

    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(150),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(150),

    CONSTRAINT fk_shop_requests_owner_request
        FOREIGN KEY (shop_owner_request_id) REFERENCES shop_owner_requests(id),

    CONSTRAINT fk_shop_requests_mall
        FOREIGN KEY (mall_id) REFERENCES malls(mall_id),

    CONSTRAINT fk_shop_requests_created_shop
        FOREIGN KEY (created_shop_id) REFERENCES shops(shop_id)
);