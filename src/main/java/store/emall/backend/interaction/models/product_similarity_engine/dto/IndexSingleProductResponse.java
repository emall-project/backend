package store.emall.backend.interaction.models.product_similarity_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexSingleProductResponse {
    private String status;
    private Boolean inserted;

    @JsonProperty("product_id")
    private Long productId;
}