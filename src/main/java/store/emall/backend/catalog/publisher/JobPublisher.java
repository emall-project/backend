package store.emall.backend.catalog.publisher;

import store.emall.backend.catalog.job.ProductJob;

public interface JobPublisher {
    void publishProductCreatedJob(ProductJob job);

    void publishProductUpdatedJob(ProductJob job);

    void publishProductDeletedJob(Long productId);
}
