package store.emall.backend.interaction.analytics.job;

import store.emall.backend.interaction.analytics.job.dto.JobAnalyticsResponse;
import store.emall.backend.interaction.analytics.job.dto.JobCountSummary;
import store.emall.backend.interaction.analytics.job.dto.JobDurationSummary;
import store.emall.backend.interaction.analytics.job.dto.JobFailureRateDto;

import java.util.List;

public interface JobAnalyticsService {
    JobCountSummary getCountSummary();
    List<JobFailureRateDto> getJobsByType();
    List<JobFailureRateDto> getJobsByStatus();
    JobDurationSummary getDurationSummary();
    JobAnalyticsResponse getOverview();
}