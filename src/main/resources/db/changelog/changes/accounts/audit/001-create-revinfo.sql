--liquibase formatted sql
--changeset lamahafiz:001-create-audit-revinfo


CREATE SEQUENCE IF NOT EXISTS accounts.revinfo_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS accounts.revinfo (
    rev INT PRIMARY KEY DEFAULT nextval('accounts.revinfo_seq'),
    revtstmp BIGINT NOT NULL
);
