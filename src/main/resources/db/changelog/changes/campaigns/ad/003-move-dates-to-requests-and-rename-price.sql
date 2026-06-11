--liquibase formatted sql
--changeset lamahafiz:003-move-dates-to-requests-and-rename-price

ALTER TABLE campaigns.ad_templates
    RENAME COLUMN price TO price_per_hour;

ALTER TABLE campaigns.ad_templates
    DROP COLUMN IF EXISTS start_date;

ALTER TABLE campaigns.ad_templates
    DROP COLUMN IF EXISTS end_date;

ALTER TABLE campaigns.ad_requests
    DROP COLUMN IF EXISTS image_url;

ALTER TABLE campaigns.ad_requests
    ADD COLUMN start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN end_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN total_price NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS ad_request_image_uuid UUID;

ALTER TABLE campaigns.ad_requests
    ALTER COLUMN start_date DROP DEFAULT,
    ALTER COLUMN end_date DROP DEFAULT,
    ALTER COLUMN total_price DROP DEFAULT;