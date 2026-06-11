--liquibase formatted sql
--changeset lamahafiz:003-create-ad-template-audit

CREATE TABLE IF NOT EXISTS campaigns.ad_templates_audit (
    rev             INT NOT NULL,
    revtype         SMALLINT,
    ad_template_id  BIGINT NOT NULL,
    name            VARCHAR(255),
    description     TEXT,
    position        VARCHAR(255),
    image_ratio     VARCHAR(50),
    price           NUMERIC(12, 2),
    start_date      DATE,
    end_date        DATE,
    status          VARCHAR(20),
    created_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_at      TIMESTAMP,
    updated_by      VARCHAR(255),

    PRIMARY KEY (ad_template_id, rev),
    CONSTRAINT fk_ad_template_audit_rev
    FOREIGN KEY (rev)
    REFERENCES campaigns.revinfo (rev)
);