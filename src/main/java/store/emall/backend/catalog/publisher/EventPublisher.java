package store.emall.backend.catalog.publisher;

import store.emall.backend.catalog.event.CatalogEvent;

public interface EventPublisher {
    void publishProductViewed(CatalogEvent event);
}