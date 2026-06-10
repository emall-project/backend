--liquibase formatted sql
--changeset lamahafiz:004-update-mall-and-restaurant-images

ALTER TABLE malls
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE malls
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE malls
    ADD COLUMN IF NOT EXISTS mall_images_uuids JSONB DEFAULT '[]';

ALTER TABLE mall_restaurants
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE mall_restaurants
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;