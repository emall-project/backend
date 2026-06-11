--liquibase formatted sql
--changeset lamahafiz:002-create-ad-request-table

CREATE SEQUENCE IF NOT EXISTS campaigns.ad_request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS campaigns.ad_requests (
    ad_request_id           BIGINT          PRIMARY KEY DEFAULT nextval('campaigns.ad_request_id_seq'),
    template_id             BIGINT          NOT NULL,
    shop_id                 BIGINT          NOT NULL,
    title                   VARCHAR(255)    NOT NULL,
    image_url               VARCHAR(500)    NOT NULL,
    status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    payment_status          VARCHAR(20)     NOT NULL DEFAULT 'UNPAID',
    paid_at                 TIMESTAMP,
    rejection_reason        TEXT,
    is_displayed            BOOLEAN         NOT NULL DEFAULT FALSE,
    payment_reminder_sent   BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(255),
    updated_at              TIMESTAMP,
    updated_by              VARCHAR(255),

    CONSTRAINT fk_ad_request_template FOREIGN KEY (template_id)
    REFERENCES campaigns.ad_templates (ad_template_id) ON DELETE RESTRICT
);