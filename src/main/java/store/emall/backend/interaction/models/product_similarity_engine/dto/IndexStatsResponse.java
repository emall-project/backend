package store.emall.backend.interaction.models.product_similarity_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexStatsResponse {
    private String status;

    @JsonProperty("indexed_products")
    private Integer indexedProducts;

    @JsonProperty("active_products")
    private Integer activeProducts;

    @JsonProperty("inactive_products")
    private Integer inactiveProducts;

    @JsonProperty("in_stock_products")
    private Integer inStockProducts;

    @JsonProperty("out_of_stock_products")
    private Integer outOfStockProducts;

    @JsonProperty("products_per_mall")
    private Map<Long, Integer> productsPerMall;
}