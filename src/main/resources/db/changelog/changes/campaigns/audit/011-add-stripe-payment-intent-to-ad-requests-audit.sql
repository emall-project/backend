--liquibase formatted sql
--changeset lamahafiz:011-add-stripe-payment-intent-to-ad-requests-audit

ALTER TABLE campaigns.ad_requests_audit
    ADD COLUMN IF NOT EXISTS stripe_payment_intent_id VARCHAR(255);