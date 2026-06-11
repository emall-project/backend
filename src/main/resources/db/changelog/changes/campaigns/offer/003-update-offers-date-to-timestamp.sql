--liquibase formatted sql
--changeset lamahafiz:003-update-offers-date-to-timestamp

ALTER TABLE campaigns.offers
    ALTER COLUMN start_date TYPE TIMESTAMP USING start_date::timestamp,
    ALTER COLUMN end_date TYPE TIMESTAMP USING end_date::timestamp;