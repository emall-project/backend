--liquibase formatted sql
--changeset lamahafiz:019-add-admin-status-to-shops-audit

ALTER TABLE accounts.shops_audit
    ADD COLUMN IF NOT EXISTS admin_status VARCHAR(20) DEFAULT 'NONE';
