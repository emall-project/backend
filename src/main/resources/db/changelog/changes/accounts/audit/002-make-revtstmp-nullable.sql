--liquibase formatted sql
--changeset lamahafiz:002-make-revtstmp-nullable

ALTER TABLE accounts.revinfo
    ALTER COLUMN revtstmp DROP NOT NULL;
