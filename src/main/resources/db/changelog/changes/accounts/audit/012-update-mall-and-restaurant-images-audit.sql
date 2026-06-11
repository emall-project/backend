--liquibase formatted sql
--changeset lamahafiz:012-update-mall-and-restaurant-images

ALTER TABLE accounts.malls_audit
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE accounts.malls_audit
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE accounts.malls_audit
    ADD COLUMN IF NOT EXISTS mall_images_uuids JSONB;

ALTER TABLE accounts.mall_restaurants_audit
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE accounts.mall_restaurants_audit
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;
