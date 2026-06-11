--liquibase formatted sql
--changeset lamahafiz:002-make-revtstmp-nullable

ALTER TABLE campaigns.revinfo
    ALTER COLUMN revtstmp DROP NOT NULL;
