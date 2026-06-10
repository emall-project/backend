package store.emall.backend.catalog.favorite;

import lombok.*;
import lombok.experimental.SuperBuilder;
import store.emall.backend.catalog.product.light.ProductLightDto;
//import store.emall.backend.catalog.product.ProductLightDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FavoriteDto {

    private Long id;

    private Long productId;

    private String user;

    private LocalDateTime addedAt;

    private ProductLightDto product;
}