--liquibase formatted sql
--changeset lamahafiz:006-create-offer-items-audit

CREATE TABLE IF NOT EXISTS campaigns.offer_items_audit (
    rev                 INT             NOT NULL,
    revtype             SMALLINT,
    offer_item_id       BIGINT          NOT NULL,
    offer_id            BIGINT,
    product_id          BIGINT,
    status              VARCHAR(20),
    created_at          TIMESTAMP,
    created_by          VARCHAR(255),
    updated_at          TIMESTAMP,
    updated_by          VARCHAR(255),

    PRIMARY KEY (offer_item_id, rev),
    CONSTRAINT fk_offer_items_audit_rev
    FOREIGN KEY (rev)
    REFERENCES campaigns.revinfo (rev)
);