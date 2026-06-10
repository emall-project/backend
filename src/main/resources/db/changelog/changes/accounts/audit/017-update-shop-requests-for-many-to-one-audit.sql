--liquibase formatted sql
--changeset lamahafiz:003-update-shop-requests-for-many-to-one-audit


ALTER TABLE audit.shop_requests_audit
    ADD COLUMN IF NOT EXISTS existing_user_id BIGINT;
