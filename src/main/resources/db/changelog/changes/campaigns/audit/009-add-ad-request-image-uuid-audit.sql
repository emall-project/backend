--liquibase formatted sql
--changeset lamahafiz:009-add-ad-request-image-uuid-audit

ALTER TABLE audit.ad_requests_audit
    ADD COLUMN IF NOT EXISTS ad_request_image_uuid UUID;