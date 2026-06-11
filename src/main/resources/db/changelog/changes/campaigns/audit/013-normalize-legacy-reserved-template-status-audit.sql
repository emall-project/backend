--liquibase formatted sql
--changeset jehadhamid:013-normalize-legacy-reserved-template-status-audit

-- Keep audited template rows readable by the current AdTemplateStatus enum.
UPDATE campaigns.ad_templates_audit
SET status = 'ACTIVE'
WHERE status = 'RESERVED';
