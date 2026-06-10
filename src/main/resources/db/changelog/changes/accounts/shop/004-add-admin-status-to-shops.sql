--liquibase formatted sql
--changeset lamahafiz:004-add-admin-status-to-shops

ALTER TABLE public.shops
    ADD COLUMN IF NOT EXISTS admin_status VARCHAR(20) NOT NULL DEFAULT 'NONE';