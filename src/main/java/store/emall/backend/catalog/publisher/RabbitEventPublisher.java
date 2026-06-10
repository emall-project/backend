package store.emall.backend.catalog.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import store.emall.backend.catalog.event.OutgoingEventConstant;
import store.emall.backend.catalog.event.CatalogEvent;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishProductViewed(CatalogEvent event) {
        rabbitTemplate.convertAndSend(
                OutgoingEventConstant.EXCHANGE_NAME,
                OutgoingEventConstant.CATALOG_PRODUCT_VIEWED_ROUTING_KEY,
                event
        );
    }

}