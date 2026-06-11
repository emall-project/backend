--liquibase formatted sql
--changeset lamahafiz:002-update-shop-images-audit

ALTER TABLE accounts.shops_audit
DROP COLUMN IF EXISTS logo_url;

ALTER TABLE accounts.shops_audit
ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE accounts.shops_audit
    ADD COLUMN IF NOT EXISTS license_image_uuid UUID;

ALTER TABLE accounts.shops_audit
    ADD COLUMN IF NOT EXISTS shop_photos_uuids JSONB;


