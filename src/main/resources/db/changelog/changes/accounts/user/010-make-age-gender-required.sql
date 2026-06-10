--liquibase formatted sql
--changeset lamahafiz:010-make-age-gender-required

ALTER TABLE users ALTER COLUMN gender TYPE VARCHAR(20);
UPDATE users SET gender = 'NOT_SPECIFIED' WHERE gender IS NULL;
UPDATE users SET age = 0 WHERE age IS NULL;

ALTER TABLE users ALTER COLUMN age SET NOT NULL;
ALTER TABLE users ALTER COLUMN gender SET NOT NULL;