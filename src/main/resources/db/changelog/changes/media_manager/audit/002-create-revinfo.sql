--liquibase formatted sql
--changeset jehadHamid:002-create-audit-revinfo


CREATE SEQUENCE IF NOT EXISTS media_manager.revinfo_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS media_manager.revinfo
(
    rev      INT PRIMARY KEY DEFAULT nextval('media_manager.revinfo_seq'),
    revtstmp BIGINT
);
