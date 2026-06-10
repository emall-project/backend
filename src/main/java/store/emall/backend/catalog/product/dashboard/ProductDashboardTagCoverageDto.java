package store.emall.backend.catalog.product.dashboard;

public record ProductDashboardTagCoverageDto(
        long productsWithTags,
        long productsWithoutTags
) {
}
