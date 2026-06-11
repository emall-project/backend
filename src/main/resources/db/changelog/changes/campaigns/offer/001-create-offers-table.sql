--liquibase formatted sql
--changeset lamahafiz:001-create-offers-table

CREATE SEQUENCE IF NOT EXISTS campaigns.offer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS campaigns.offers (
    offer_id        BIGINT          PRIMARY KEY DEFAULT nextval('campaigns.offer_id_seq'),
    shop_id         BIGINT          NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    description     TEXT,
    discount_type   VARCHAR(20)     NOT NULL,
    discount_value  NUMERIC(10, 2)  NOT NULL,
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'INACTIVE',
    max_uses        INT,
    current_uses    INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(255)
);