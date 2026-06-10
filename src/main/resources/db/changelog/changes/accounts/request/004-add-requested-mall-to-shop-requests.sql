--liquibase formatted sql
--changeset lamahafiz:004-add-requested-mall-to-shop-requests

ALTER TABLE shop_requests
    ALTER COLUMN mall_id DROP NOT NULL;

ALTER TABLE shop_requests
    ADD COLUMN IF NOT EXISTS requested_mall_name VARCHAR(255);

ALTER TABLE shop_requests
    ADD COLUMN IF NOT EXISTS requested_mall_city_id BIGINT;

ALTER TABLE shop_requests
    ADD CONSTRAINT fk_shop_requests_requested_city
        FOREIGN KEY (requested_mall_city_id) REFERENCES cities(city_id);

ALTER TABLE shop_requests
    ADD CONSTRAINT chk_shop_request_mall_xor
        CHECK (
            (mall_id IS NOT NULL AND requested_mall_name IS NULL AND requested_mall_city_id IS NULL)
                OR
            (mall_id IS NULL AND requested_mall_name IS NOT NULL AND requested_mall_city_id IS NOT NULL)
        );