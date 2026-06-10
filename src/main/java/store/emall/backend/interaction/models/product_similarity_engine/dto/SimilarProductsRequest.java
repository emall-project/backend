package store.emall.backend.interaction.models.product_similarity_engine.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarProductsRequest {
    private Long productId;
    private ProductSimilarity product;
    private Integer topK;
    private Boolean sameMallOnly;
    private Boolean inStockOnly;
    private Boolean activeOnly;
}