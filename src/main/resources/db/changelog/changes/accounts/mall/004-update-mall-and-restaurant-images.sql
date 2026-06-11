--liquibase formatted sql
--changeset lamahafiz:004-update-mall-and-restaurant-images

ALTER TABLE accounts.malls
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE accounts.malls
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE accounts.malls
    ADD COLUMN IF NOT EXISTS mall_images_uuids JSONB DEFAULT '[]';

ALTER TABLE accounts.mall_restaurants
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE accounts.mall_restaurants
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;