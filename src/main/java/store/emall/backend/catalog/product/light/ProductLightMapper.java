package store.emall.backend.catalog.product.light;

import store.emall.backend.catalog.product.Product;
import store.emall.backend.catalog.product.product_variant.ProductVariant;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.Map;
import java.util.UUID;

public class ProductLightMapper {
    public static ProductLightDto toDtoLight(Product product) {
        FileDto medium= new FileDto();
        ProductVariant defaultVariant = product.getDefaultVariant();
        medium.setId(defaultVariant.getMedia().getFirst().getMediumId());
        return ProductLightDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .basePrice(defaultVariant.getBasePrice())
                .defaultVariantId(defaultVariant.getId())
                .medium(medium)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .isActive(product.getIsActive())
                .variantsCount(product.getVariants() != null ? (long) product.getVariants().size() : 0L)
                .build();
    }


    public static ProductLightDto toProductLightDto(
            Long productId,
            Map<Long, ProductLightRow> productLightRowMap,
            Map<UUID, FileDto> mediaMap
    ) {
        ProductLightRow row = productLightRowMap.get(productId);

        if (row == null) {
            return ProductLightDto.builder()
                    .id(productId)
                    .build();
        }

        ProductLightDto dto = ProductLightDto.builder()
                .id(row.getProductId())
                .defaultVariantId(row.getDefaultVariantId())
                .basePrice(row.getBasePrice())
                .name(row.getProductName())
                .slug(row.getProductSlug())
                .categoryName(row.getCategoryName())
                .brandName(row.getBrandName())
                .isActive(row.getIsActive())
                .variantsCount(row.getVariantsCount())
                .build();

        if (row.getMediumId() != null) {
            FileDto media = mediaMap.get(row.getMediumId());

            if (media != null) {
                dto.setMedium(media);
            } else {
                FileDto mediumRef = new FileDto();
                mediumRef.setId(row.getMediumId());
                dto.setMedium(mediumRef);
            }
        }

        return dto;
    }

}
