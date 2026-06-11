--liquibase formatted sql
--changeset lamahafiz:010-make-age-gender-required

ALTER TABLE accounts.users ALTER COLUMN gender TYPE VARCHAR(20);
UPDATE accounts.users SET gender = 'NOT_SPECIFIED' WHERE gender IS NULL;
UPDATE accounts.users SET age = 0 WHERE age IS NULL;

ALTER TABLE accounts.users ALTER COLUMN age SET NOT NULL;
ALTER TABLE accounts.users ALTER COLUMN gender SET NOT NULL;