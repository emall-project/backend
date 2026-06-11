--liquibase formatted sql
--changeset lamahafiz:012-create-ad-payments-audit-table

CREATE TABLE IF NOT EXISTS campaigns.ad_payments_audit (
    rev                      INT             NOT NULL,
    revtype                  SMALLINT,
    payment_id               BIGINT          NOT NULL,
    ad_request_id            BIGINT,
    amount                   NUMERIC(12, 2),
    currency                 VARCHAR(10),
    payment_date             TIMESTAMP,
    payment_method           VARCHAR(20),
    payment_status           VARCHAR(20),
    stripe_payment_intent_id VARCHAR(255),
    invoice_url              TEXT,
    failure_reason           TEXT,
    created_at               TIMESTAMP,
    created_by               VARCHAR(255),
    updated_at               TIMESTAMP,
    updated_by               VARCHAR(255),
    PRIMARY KEY (payment_id, rev),
    CONSTRAINT fk_ad_pay_audit_rev FOREIGN KEY (rev) REFERENCES accounts.revinfo(rev)
);