--liquibase formatted sql
--changeset lamahafiz:012-update-mall-and-restaurant-images

ALTER TABLE audit.malls_audit
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE audit.malls_audit
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;

ALTER TABLE audit.malls_audit
    ADD COLUMN IF NOT EXISTS mall_images_uuids JSONB;

ALTER TABLE audit.mall_restaurants_audit
    DROP COLUMN IF EXISTS logo_url;

ALTER TABLE audit.mall_restaurants_audit
    ALTER COLUMN logo_uuid TYPE UUID USING logo_uuid::uuid;
