package store.emall.backend.interaction.models.product_similarity_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersistIndexResponse {
    private String status;
    private String message;
}