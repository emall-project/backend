package store.emall.backend.interaction.jobs.ingestion.catalog.product;

public interface ProductJobService {
    void productCreatedJob(Product product);

    void productUpdatedJob(Product product);

    void productDeletedJob(Long productId);

}
