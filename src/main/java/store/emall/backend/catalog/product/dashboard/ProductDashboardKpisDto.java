package store.emall.backend.catalog.product.dashboard;

public record ProductDashboardKpisDto(
        long totalProducts,
        long activeProducts,
        long inactiveProducts
) {
}
