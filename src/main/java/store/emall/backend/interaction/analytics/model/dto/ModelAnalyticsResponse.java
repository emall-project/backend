package store.emall.backend.interaction.analytics.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelAnalyticsResponse {
    private ModelUsageSummary usageSummary;
    private ModelLatencyDto latency;
    private List<ModelFailureRateDto> failureRates;
    private List<ModelCallsPerHourDto> callsPerHour;
}