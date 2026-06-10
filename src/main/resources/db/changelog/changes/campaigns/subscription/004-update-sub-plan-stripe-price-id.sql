--liquibase formatted sql
--changeset lamahafiz:004-update-sub-plan-stripe-price-id

UPDATE public.subscription_plans
SET stripe_price_id = 'price_1TNEqRFFCeiib8ugt5ziBgRT'
WHERE plan_type = 'MONTHLY';

UPDATE public.subscription_plans
SET stripe_price_id = 'price_1TNErhFFCeiib8ugC5ej5zmF'
WHERE plan_type = 'YEARLY';

