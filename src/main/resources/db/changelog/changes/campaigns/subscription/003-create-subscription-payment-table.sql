--liquibase formatted sql
--changeset lamahafiz:003-create-subscription-payment-table

CREATE SEQUENCE IF NOT EXISTS subscription_payment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS public.subscription_payments (
    payment_id        BIGINT          PRIMARY KEY DEFAULT nextval('subscription_payment_id_seq'),
    subscription_id   BIGINT          NOT NULL REFERENCES public.shop_subscriptions(subscription_id),
    amount            NUMERIC(12, 2)  NOT NULL,
    currency          VARCHAR(10)     NOT NULL,
    payment_date      TIMESTAMP       NOT NULL,
    payment_method    VARCHAR(20)     NOT NULL,
    payment_status    VARCHAR(20)     NOT NULL,
    transaction_id    VARCHAR(255),
    invoice_url       TEXT,
    failure_reason    TEXT,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        VARCHAR(255),
    updated_at        TIMESTAMP,
    updated_by        VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_sub_pay_transaction_id ON public.subscription_payments(transaction_id);