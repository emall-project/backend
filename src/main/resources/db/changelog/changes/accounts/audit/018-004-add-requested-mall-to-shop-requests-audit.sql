--liquibase formatted sql
--changeset lamahafiz:004-add-requested-mall-to-shop-requests-audit

ALTER TABLE audit.shop_requests_audit
    ADD COLUMN IF NOT EXISTS requested_mall_name VARCHAR(255);

ALTER TABLE audit.shop_requests_audit
    ADD COLUMN IF NOT EXISTS requested_mall_city_id BIGINT;