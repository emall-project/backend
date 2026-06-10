package store.emall.backend.orderhub.dashboard.section;

import lombok.*;
import store.emall.backend.orderhub.client.catalog.ProductLightDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInsightDto {
    private ProductLightDto product;
    private Long orderedQuantity;
}
