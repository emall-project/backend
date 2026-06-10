--liquibase formatted sql
--changeset lamahafiz:011-update-admin-password

UPDATE users
SET
    password = '$2a$10$oIkdw.7Xvfd2u7CfIKYRLuamiOAiOvtPrEZ3YrBeX4fqZ4xy1WscC',
    updated_at = CURRENT_TIMESTAMP,
    updated_by = 'system'
WHERE username = 'admin'
  AND email = 'admin@emalls.com';