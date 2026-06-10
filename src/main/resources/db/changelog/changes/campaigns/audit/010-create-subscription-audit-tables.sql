--liquibase formatted sql
--changeset emall:010-create-subscription-audit-tables

CREATE TABLE IF NOT EXISTS audit.subscription_plans_audit (
    rev                   INT             NOT NULL,
    revtype               SMALLINT,
    subscription_plan_id  BIGINT          NOT NULL,
    name                  VARCHAR(100),
    plan_type             VARCHAR(20),
    duration_months       INTEGER,
    price                 NUMERIC(12, 2),
    currency              VARCHAR(10),
    stripe_price_id       VARCHAR(255),
    is_active             BOOLEAN,
    created_at            TIMESTAMP,
    created_by            VARCHAR(255),
    updated_at            TIMESTAMP,
    updated_by            VARCHAR(255),
    PRIMARY KEY (subscription_plan_id, rev),
    CONSTRAINT fk_sub_plan_audit_rev FOREIGN KEY (rev) REFERENCES audit.revinfo(rev)
);

CREATE TABLE IF NOT EXISTS audit.shop_subscriptions_audit (
    rev                     INT             NOT NULL,
    revtype                 SMALLINT,
    subscription_id         BIGINT          NOT NULL,
    shop_id                 BIGINT,
    plan_id                 BIGINT,
    status                  VARCHAR(20),
    start_date              DATE,
    end_date                DATE,
    trial_end_date          DATE,
    price_paid              NUMERIC(12, 2),
    auto_renew              BOOLEAN,
    stripe_customer_id      VARCHAR(255),
    stripe_subscription_id  VARCHAR(255),
    cancelled_at            TIMESTAMP,
    suspended_at            TIMESTAMP,
    payment_failure_count   INTEGER,
    created_at              TIMESTAMP,
    created_by              VARCHAR(255),
    updated_at              TIMESTAMP,
    updated_by              VARCHAR(255),
    PRIMARY KEY (subscription_id, rev),
    CONSTRAINT fk_shop_sub_audit_rev FOREIGN KEY (rev) REFERENCES audit.revinfo(rev)
);

CREATE TABLE IF NOT EXISTS audit.subscription_payments_audit (
    rev               INT             NOT NULL,
    revtype           SMALLINT,
    payment_id        BIGINT          NOT NULL,
    subscription_id   BIGINT,
    amount            NUMERIC(12, 2),
    currency          VARCHAR(10),
    payment_date      TIMESTAMP,
    payment_method    VARCHAR(20),
    payment_status    VARCHAR(20),
    transaction_id    VARCHAR(255),
    invoice_url       TEXT,
    failure_reason    TEXT,
    created_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_at        TIMESTAMP,
    updated_by        VARCHAR(255),
    PRIMARY KEY (payment_id, rev),
    CONSTRAINT fk_sub_pay_audit_rev FOREIGN KEY (rev) REFERENCES audit.revinfo(rev)
);