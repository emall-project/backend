--liquibase formatted sql
--changeset lamahafiz:002-update-shop-images

ALTER TABLE accounts.shops
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE accounts.shops
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE accounts.shops
    ADD COLUMN IF NOT EXISTS license_image_uuid UUID NOT NULL;

ALTER TABLE accounts.shops
    ADD COLUMN IF NOT EXISTS shop_photos_uuids JSONB NOT NULL DEFAULT '[]';


