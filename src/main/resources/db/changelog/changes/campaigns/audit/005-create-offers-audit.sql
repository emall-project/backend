--liquibase formatted sql
--changeset lamahafiz:005-create-offers-audit

CREATE TABLE IF NOT EXISTS campaigns.offers_audit (
    rev             INT             NOT NULL,
    revtype         SMALLINT,
    offer_id        BIGINT          NOT NULL,
    shop_id         BIGINT,
    title           VARCHAR(255),
    description     TEXT,
    discount_type   VARCHAR(20),
    discount_value  NUMERIC(10, 2),
    start_date      DATE,
    end_date        DATE,
    status          VARCHAR(20),
    max_uses        INT,
    current_uses    INT,
    created_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(255),

    PRIMARY KEY (offer_id, rev),
    CONSTRAINT fk_offers_audit_rev
    FOREIGN KEY (rev)
    REFERENCES campaigns.revinfo (rev)
);