package store.emall.backend.catalog.favorite;

import store.emall.backend.catalog.product.light.ProductLightDto;
import store.emall.backend.catalog.product.light.ProductLightMapper;

public class FavoriteMapper {

    public static FavoriteDto toDto(Favorite entity) {
        if (entity == null) {
            return null;
        }
        ProductLightDto product = null;
        if (entity.getProduct() != null) {
            product = ProductLightMapper.toDtoLight(entity.getProduct());
        }
        return FavoriteDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .user(entity.getUser())
                .addedAt(entity.getAddedAt())
                .product(product)
                .build();

    }
}