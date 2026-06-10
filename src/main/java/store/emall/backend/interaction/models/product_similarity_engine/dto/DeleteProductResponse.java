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
public class DeleteProductResponse {
    private String status;

    @JsonProperty("product_id")
    private Long productId;

    private Boolean removed;
    private String note;
}