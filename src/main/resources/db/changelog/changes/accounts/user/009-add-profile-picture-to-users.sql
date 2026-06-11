--liquibase formatted sql
--changeset lamahafiz:009-add-profile-picture-to-users

ALTER TABLE accounts.users
    ADD COLUMN IF NOT EXISTS profile_picture_uuid UUID;