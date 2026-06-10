--liquibase formatted sql
--changeset lamahafiz:004-create-ad-request-audit

CREATE TABLE IF NOT EXISTS audit.ad_requests_audit (
    rev                     INT NOT NULL,
    revtype                 SMALLINT,
    ad_request_id           BIGINT NOT NULL,
    template_id             BIGINT,
    shop_id                 BIGINT,
    title                   VARCHAR(255),
    image_url               VARCHAR(500),
    status                  VARCHAR(20),
    payment_status          VARCHAR(20),
    paid_at                 TIMESTAMP,
    rejection_reason        TEXT,
    is_displayed            BOOLEAN,
    payment_reminder_sent   BOOLEAN,
    created_at              TIMESTAMP,
    created_by              VARCHAR(255),
    updated_at              TIMESTAMP,
    updated_by              VARCHAR(255),

    PRIMARY KEY (ad_request_id, rev),
    CONSTRAINT fk_ad_request_audit_rev
    FOREIGN KEY (rev)
    REFERENCES audit.revinfo (rev)
);
