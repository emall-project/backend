package store.emall.backend.interaction.jobs.ingestion.catalog.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import store.emall.backend.interaction.jobs.JobQueue;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductJobConsumer {

    private final ProductJobService productJobService;

    @RabbitListener(
            queues = JobQueue.CATALOG_PRODUCT_CREATED,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeProductCreatedJob(Product product) {
        log.info("Received product created job for product id {}", product.getId());
        productJobService.productCreatedJob(product);
    }

    @RabbitListener(
            queues = JobQueue.CATALOG_PRODUCT_UPDATED,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeProductUpdatedJob(Product product) {
        log.info("Received product updated job for product id {}", product.getId());
        productJobService.productUpdatedJob(product);
    }

    @RabbitListener(
            queues = JobQueue.CATALOG_PRODUCT_DELETED,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consumeProductDeletedJob(Long productId) {
        log.info("Received product deleted job for product id {}", productId);
        productJobService.productDeletedJob(productId);
    }
}
