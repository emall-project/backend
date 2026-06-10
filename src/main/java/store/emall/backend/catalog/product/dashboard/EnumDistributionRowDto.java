package store.emall.backend.catalog.product.dashboard;

public record EnumDistributionRowDto(
        String key,
        long totalProducts,
        long activeProducts
) {
}
