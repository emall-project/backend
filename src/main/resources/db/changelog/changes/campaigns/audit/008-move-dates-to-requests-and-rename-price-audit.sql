--liquibase formatted sql
--changeset lamahafiz:008-move-dates-to-requests-and-rename-price-audit


ALTER TABLE audit.ad_templates_audit
    RENAME COLUMN price TO price_per_hour;

ALTER TABLE audit.ad_templates_audit
    DROP COLUMN IF EXISTS start_date;

ALTER TABLE audit.ad_templates_audit
    DROP COLUMN IF EXISTS end_date;

ALTER TABLE audit.ad_requests_audit
    DROP COLUMN IF EXISTS image_url;

ALTER TABLE audit.ad_requests_audit
    ADD COLUMN start_date TIMESTAMP,
    ADD COLUMN end_date TIMESTAMP,
    ADD COLUMN total_price NUMERIC(12, 2);