--liquibase formatted sql
--changeset lamahafiz:003-insert-roles

INSERT INTO roles (role_id, code, name, created_at, created_by)
VALUES
    (NEXTVAL('role_id_seq'), 'ROLE_ADMIN', 'Admin', CURRENT_TIMESTAMP, 'admin'),
    (NEXTVAL('role_id_seq'), 'ROLE_SHOP_OWNER', 'Shop Owner', CURRENT_TIMESTAMP, 'admin'),
    (NEXTVAL('role_id_seq'), 'ROLE_CUSTOMER', 'Customer', CURRENT_TIMESTAMP, 'admin');
