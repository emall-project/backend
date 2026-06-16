package store.emall.backend.interaction.interaction;

import store.emall.backend.interaction.interaction.ingestion.catalog.CatalogEvent;

public interface InteractionEventService {


    InteractionEvent shopCatalogEvent(CatalogEvent event, String routingKey);

}