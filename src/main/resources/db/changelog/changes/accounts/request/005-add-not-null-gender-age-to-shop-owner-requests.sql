--liquibase formatted sql
--changeset lamahafiz:005-add-not-null-gender-age-to-shop-owner-requests

ALTER TABLE shop_owner_requests
    ALTER COLUMN gender SET NOT NULL;

ALTER TABLE shop_owner_requests
    ALTER COLUMN age SET NOT NULL;