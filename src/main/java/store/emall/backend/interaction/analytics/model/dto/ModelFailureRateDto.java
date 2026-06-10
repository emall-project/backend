package store.emall.backend.interaction.analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFailureRateDto {
    private String modelName;
    private long totalCalls;
    private long failedCalls;
    private double failureRate;
}