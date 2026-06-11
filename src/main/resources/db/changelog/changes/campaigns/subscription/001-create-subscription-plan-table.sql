--liquibase formatted sql
--changeset lamahafiz:001-create-subscription-plan-table

CREATE SEQUENCE IF NOT EXISTS campaigns.subscription_plan_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS campaigns.subscription_plans (
    subscription_plan_id  BIGINT          PRIMARY KEY DEFAULT nextval('campaigns.subscription_plan_id_seq'),
    name                  VARCHAR(100)    NOT NULL,
    plan_type             VARCHAR(20)     NOT NULL,
    duration_months       INTEGER         NOT NULL,
    price                 NUMERIC(12, 2)  NOT NULL,
    currency              VARCHAR(10)     NOT NULL DEFAULT 'USD',
    stripe_price_id       VARCHAR(255)    NOT NULL,
    is_active             BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(255),
    updated_at            TIMESTAMP,
    updated_by            VARCHAR(255)
);

INSERT INTO campaigns.subscription_plans
(name, plan_type, duration_months, price, currency, stripe_price_id, is_active)
VALUES
    ('Monthly Plan', 'MONTHLY', 1,    9.99, 'USD', 'prod_ULwjCUAknZzaZm', TRUE),
    ('Yearly Plan',  'YEARLY',  12,  99.99, 'USD', 'prod_ULwlkOf0muTRw1',  TRUE);