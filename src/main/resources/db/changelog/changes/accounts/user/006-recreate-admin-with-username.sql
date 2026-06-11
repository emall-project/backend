--liquibase formatted sql
--changeset lamahafiz:006-recreate-admin-with-username

DELETE FROM accounts.users
    WHERE email = 'admin@emalls.com' OR full_name = 'admin' OR phone_number = '+970-0599000000';

INSERT INTO accounts.users (
    user_id,
    username,
    full_name,
    email,
    phone_number,
    password,
    role_id,
    is_active,
    created_at,
    created_by
)
VALUES (
    NEXTVAL('accounts.user_id_seq'),
    'admin',
    'System Administrator',
    'admin@emalls.com',
    '+970-0599000000',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOa1HnD6nV9z1R3Y1FJxR5E8u6YxX1KxG',
    (SELECT role_id FROM accounts.roles WHERE code = 'ROLE_ADMIN'),
    TRUE,
    CURRENT_TIMESTAMP,
    'system'
);
