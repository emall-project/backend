--liquibase formatted sql
--changeset lamahafiz:002-create-offer-items-table

CREATE SEQUENCE IF NOT EXISTS campaigns.offer_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS campaigns.offer_items (
    offer_item_id       BIGINT          PRIMARY KEY DEFAULT nextval('campaigns.offer_item_id_seq'),
    offer_id            BIGINT          NOT NULL,
    product_id          BIGINT          NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(255),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(255),

    CONSTRAINT uk_offer_product     UNIQUE (offer_id, product_id),
    CONSTRAINT fk_offer_item_offer  FOREIGN KEY (offer_id)
        REFERENCES campaigns.offers (offer_id) ON DELETE CASCADE
);