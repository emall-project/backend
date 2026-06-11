--liquibase formatted sql
--changeset lamahafiz:002-create-shop-subscription-table

CREATE SEQUENCE IF NOT EXISTS campaigns.shop_subscription_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS campaigns.shop_subscriptions (
    subscription_id         BIGINT          PRIMARY KEY DEFAULT nextval('campaigns.shop_subscription_id_seq'),
    shop_id                 BIGINT          NOT NULL UNIQUE,
    plan_id                 BIGINT          REFERENCES campaigns.subscription_plans(subscription_plan_id),
    status                  VARCHAR(20)     NOT NULL,
    start_date              DATE            NOT NULL,
    end_date                DATE,
    trial_end_date          DATE            NOT NULL,
    price_paid              NUMERIC(12, 2),
    auto_renew              BOOLEAN         NOT NULL DEFAULT FALSE,
    stripe_customer_id      VARCHAR(255),
    stripe_subscription_id  VARCHAR(255),
    cancelled_at            TIMESTAMP,
    suspended_at            TIMESTAMP,
    payment_failure_count   INTEGER         NOT NULL DEFAULT 0,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    updated_at              TIMESTAMP,
    updated_by              VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_shop_sub_shop_id ON campaigns.shop_subscriptions(shop_id);
CREATE INDEX IF NOT EXISTS idx_shop_sub_status ON campaigns.shop_subscriptions(status);
CREATE INDEX IF NOT EXISTS idx_shop_sub_trial_end ON campaigns.shop_subscriptions(trial_end_date);
CREATE INDEX IF NOT EXISTS idx_shop_sub_end_date ON campaigns.shop_subscriptions(end_date);
CREATE INDEX IF NOT EXISTS idx_shop_sub_suspended_at ON campaigns.shop_subscriptions(suspended_at);
CREATE INDEX IF NOT EXISTS idx_shop_sub_stripe_sub_id ON campaigns.shop_subscriptions(stripe_subscription_id);
