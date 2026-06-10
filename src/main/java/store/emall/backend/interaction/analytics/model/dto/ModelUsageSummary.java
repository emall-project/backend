package store.emall.backend.interaction.analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelUsageSummary {
    private long totalCalls;
    private long successfulCalls;
    private long failedCalls;
    private double failureRate;
}