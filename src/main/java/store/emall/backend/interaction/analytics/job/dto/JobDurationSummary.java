package store.emall.backend.interaction.analytics.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDurationSummary {
    private double averageDurationMs;
    private long maxDurationMs;
}