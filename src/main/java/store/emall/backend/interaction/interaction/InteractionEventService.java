package store.emall.backend.interaction.interaction;

import store.emall.backend.interaction.interaction.ingestion.catalog.CatalogEvent;

public interface InteractionEventService {


    InteractionEvent storeCatalogEvent(CatalogEvent event, String routingKey);

}