-- liquibase formatted sql

-- changeset jehad:001-create-interaction-events
CREATE TABLE IF NOT EXISTS interaction_events
(
    id             BIGSERIAL PRIMARY KEY,
    occurred_by           VARCHAR(50)              NOT NULL,
--     product_id     BIGINT                   NULL,
--     order_id       BIGINT                   NULL,
--     campaign_id    BIGINT                   NULL,
    entity_id      BIGINT                   NULL,
    event_type     VARCHAR(64)              NOT NULL,
    source_service VARCHAR(100)             NOT NULL,
    routing_key    VARCHAR(150)             NOT NULL,
    occurred_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    correlation_id VARCHAR(100)             NULL,
    metadata       TEXT                     NULL
);

-- -- changeset jehad:002-index-user-id
-- CREATE INDEX IF NOT EXISTS idx_interaction_events_user
--     ON interaction_events (occurred_by);
--
-- -- changeset jehad:003-index-product-id
-- CREATE INDEX IF NOT EXISTS idx_interaction_events_product_id
--     ON interaction_events (product_id);
--
-- -- changeset jehad:004-index-campaign-id
-- CREATE INDEX IF NOT EXISTS idx_interaction_events_campaign_id
--     ON interaction_events (campaign_id);

CREATE INDEX IF NOT EXISTS idx_interaction_events_campaign_id
    ON interaction_events (entity_id);

-- changeset jehad:005-index-order-id
-- CREATE INDEX IF NOT EXISTS idx_interaction_events_order_id
--     ON interaction_events (order_id);

-- changeset jehad:006-index-event-type
CREATE INDEX IF NOT EXISTS idx_interaction_events_event_type
    ON interaction_events (event_type);

-- changeset jehad:007-index-occurred-at
CREATE INDEX IF NOT EXISTS idx_interaction_events_occurred_at
    ON interaction_events (occurred_at);

-- changeset jehad:008-index-user-occurred-at
CREATE INDEX IF NOT EXISTS idx_interaction_events_user_occurred_at
    ON interaction_events (occurred_by, occurred_at DESC);

-- changeset jehad:009-index-product-event-type
CREATE INDEX IF NOT EXISTS idx_interaction_events_product_event_type
    ON interaction_events (entity_id, event_type);