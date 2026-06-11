--liquibase formatted sql
--changeset jehadhamid:006-normalize-legacy-reserved-template-status

-- RESERVED was removed from AdTemplateStatus when template availability became
-- time-slot based. Older rows may still contain the legacy value, which makes
-- Hibernate fail enum hydration before application code can handle the record.
UPDATE campaigns.ad_templates
SET status = 'ACTIVE'
WHERE status = 'RESERVED';
