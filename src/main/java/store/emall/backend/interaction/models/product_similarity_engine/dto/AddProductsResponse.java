package store.emall.backend.interaction.models.product_similarity_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductsResponse {
    private String status;

    @JsonProperty("added_products")
    private List<Long> addedProducts;

    @JsonProperty("skipped_products")
    private List<Long> skippedProducts;
}