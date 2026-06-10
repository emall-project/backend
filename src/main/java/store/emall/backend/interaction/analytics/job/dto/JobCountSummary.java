package store.emall.backend.interaction.analytics.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCountSummary {
    private long totalJobs;
    private long successfulJobs;
    private long failedJobs;
    private long skippedJobs;
    private long startedJobs;
}