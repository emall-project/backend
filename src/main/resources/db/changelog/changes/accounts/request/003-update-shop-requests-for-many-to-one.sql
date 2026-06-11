--liquibase formatted sql
--changeset lamahafiz:003-update-shop-requests-for-many-to-one

ALTER TABLE accounts.shop_requests
    ALTER COLUMN shop_owner_request_id DROP NOT NULL;

ALTER TABLE accounts.shop_requests
    ADD COLUMN IF NOT EXISTS existing_user_id BIGINT;

ALTER TABLE accounts.shop_requests
    ADD CONSTRAINT fk_shop_requests_existing_user
        FOREIGN KEY (existing_user_id) REFERENCES accounts.users(user_id);

ALTER TABLE accounts.shop_requests
    ADD CONSTRAINT chk_shop_request_owner_xor
        CHECK (
            (shop_owner_request_id IS NOT NULL AND existing_user_id IS NULL)
                OR
            (shop_owner_request_id IS NULL AND existing_user_id IS NOT NULL)
        );