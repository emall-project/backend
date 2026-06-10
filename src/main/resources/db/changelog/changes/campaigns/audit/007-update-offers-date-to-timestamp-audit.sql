--liquibase formatted sql
--changeset lamahafiz:007-update-offers-date-to-timestamp-audit

ALTER TABLE audit.offers_audit
ALTER COLUMN start_date TYPE TIMESTAMP USING start_date::timestamp,
    ALTER COLUMN end_date TYPE TIMESTAMP USING end_date::timestamp;