--liquibase formatted sql
--changeset lamahafiz:001-create-shop-owner-requests-table

CREATE SEQUENCE IF NOT EXISTS shop_owner_request_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS shop_owner_requests (
    id               BIGINT PRIMARY KEY DEFAULT NEXTVAL('shop_owner_request_id_seq'),
    full_name        VARCHAR(255),
    username         VARCHAR(150)  NOT NULL UNIQUE,
    email            VARCHAR(150)  UNIQUE,
    phone_number     VARCHAR(25)   NOT NULL UNIQUE,
    password         VARCHAR(255)  NOT NULL,
    gender           VARCHAR(10),
    age              INTEGER,
    national_id_number VARCHAR(20) UNIQUE,
    profile_picture_uuid UUID,
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    created_user_id  BIGINT,

    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(150),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(150),

    CONSTRAINT fk_shop_owner_requests_created_user
        FOREIGN KEY (created_user_id) REFERENCES users(user_id)
);