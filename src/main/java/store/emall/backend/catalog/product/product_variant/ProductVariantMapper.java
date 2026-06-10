package store.emall.backend.catalog.product.product_variant;

import lombok.extern.slf4j.Slf4j;
import store.emall.backend.catalog.product.Product;
import store.emall.backend.catalog.product.product_media.ProductMediumMapper;
import store.emall.backend.catalog.product.product_variant.variant_attribute.VariantAttributeMapper;

@Slf4j
public class ProductVariantMapper {
    public static ProductVariantDto toDto(ProductVariant entity) {
        return ProductVariantDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .basePrice(entity.getBasePrice())
                .isDefault(entity.getIsDefault())

                .createdAt(entity.getCreatedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .updatedAt(entity.getUpdatedAt())
                .media(entity.getMedia() != null ?
                        entity.getMedia().stream().map(ProductMediumMapper::toDto).toList()
                        : null
                )
                .attributes(entity.getVariantAttributes() != null ?
                        entity.getVariantAttributes().stream().map(VariantAttributeMapper::toDto).toList()
                        : null
                )
                .build();
    }

    public static ProductVariant toEntity(ProductVariantDto dto, Product product) {
        return ProductVariant.builder()
                .id(dto.getId())
                .name(dto.getName())
                .basePrice(dto.getBasePrice())
                .isDefault(dto.getIsDefault())
                .product(product)
                .build();
    }
}
