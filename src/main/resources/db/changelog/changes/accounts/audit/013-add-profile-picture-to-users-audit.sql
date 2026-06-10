--liquibase formatted sql
--changeset lamahafiz:009-add-profile-picture-to-users-audit

ALTER TABLE audit.users_audit
    ADD COLUMN IF NOT EXISTS profile_picture_uuid UUID;