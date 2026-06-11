--liquibase formatted sql
--changeset lamahafiz:004-add-stripe-payment-intent-to-ad-requests

ALTER TABLE campaigns.ad_requests
    ADD COLUMN IF NOT EXISTS stripe_payment_intent_id VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_ad_req_stripe_pi_id
    ON campaigns.ad_requests(stripe_payment_intent_id);