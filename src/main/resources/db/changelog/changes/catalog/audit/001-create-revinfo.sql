-- liquibase formatted sql
-- changeset JehadHamid:001-create-audit-revinfo runOnChange:true


CREATE SEQUENCE IF NOT EXISTS catalog.revinfo_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS catalog.revinfo
(
    rev      INT PRIMARY KEY DEFAULT nextval('catalog.revinfo_seq'),
    revtstmp BIGINT
);
