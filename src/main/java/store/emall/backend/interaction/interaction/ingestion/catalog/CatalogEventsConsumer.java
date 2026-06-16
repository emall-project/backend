package store.emall.backend.interaction.interaction.ingestion.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import store.emall.backend.interaction.interaction.EventQueue;
import store.emall.backend.interaction.interaction.InteractionEventService;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class CatalogEventsConsumer {

    private final InteractionEventService interactionEventService;

    @RabbitListener(
            queues = EventQueue.CATALOG,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeCatalogEvent(
            CatalogEvent catalogEvent,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) {
        log.info("Received catalog event with routingKey={}", routingKey);

        interactionEventService.shopCatalogEvent(catalogEvent, routingKey);
    }
}
