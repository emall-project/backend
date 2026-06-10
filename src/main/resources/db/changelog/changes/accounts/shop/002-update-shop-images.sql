--liquibase formatted sql
--changeset lamahafiz:002-update-shop-images

ALTER TABLE shops
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE shops
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE shops
    ADD COLUMN IF NOT EXISTS license_image_uuid UUID NOT NULL;

ALTER TABLE shops
    ADD COLUMN IF NOT EXISTS shop_photos_uuids JSONB NOT NULL DEFAULT '[]';


