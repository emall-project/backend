--liquibase formatted sql
--changeset lamahafiz:009-add-profile-picture-to-users

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS profile_picture_uuid UUID;