--liquibase formatted sql
--changeset lamahafiz:005-create-ad-payments-table

CREATE SEQUENCE IF NOT EXISTS ad_payment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS public.ad_payments (
    payment_id              BIGINT          PRIMARY KEY DEFAULT nextval('ad_payment_id_seq'),
    ad_request_id           BIGINT          NOT NULL REFERENCES public.ad_requests(ad_request_id),
    amount                  NUMERIC(12, 2)  NOT NULL,
    currency                VARCHAR(10)     NOT NULL DEFAULT 'USD',
    payment_date            TIMESTAMP       NOT NULL,
    payment_method          VARCHAR(20)     NOT NULL,
    payment_status          VARCHAR(20)     NOT NULL,
    stripe_payment_intent_id VARCHAR(255),
    invoice_url             TEXT,
    failure_reason          TEXT,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    updated_at              TIMESTAMP,
    updated_by              VARCHAR(255)
    );

CREATE INDEX IF NOT EXISTS idx_ad_pay_request_id ON public.ad_payments(ad_request_id);
CREATE INDEX IF NOT EXISTS idx_ad_pay_stripe_pi  ON public.ad_payments(stripe_payment_intent_id);