package store.emall.backend.interaction.analytics.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalyticsResponse {
    private JobCountSummary countSummary;
    private JobDurationSummary durationSummary;
    private List<JobFailureRateDto> jobsByType;
    private List<JobFailureRateDto> jobsByStatus;
}