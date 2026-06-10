package store.emall.backend.interaction.analytics.job;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.interaction.analytics.job.dto.JobAnalyticsResponse;
import store.emall.backend.interaction.analytics.job.dto.JobCountSummary;
import store.emall.backend.interaction.analytics.job.dto.JobDurationSummary;
import store.emall.backend.interaction.analytics.job.dto.JobFailureRateDto;
import store.emall.backend.common.response.EMallsResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/analytics/jobs")
@RequiredArgsConstructor
public class JobAnalyticsController {

    private final JobAnalyticsService jobAnalyticsService;

    @GetMapping("/overview")
    public EMallsResponseEntity<JobAnalyticsResponse> overview() {
        return EMallsResponseEntity.ok(jobAnalyticsService.getOverview());
    }

    @GetMapping("/count-summary")
    public EMallsResponseEntity<JobCountSummary> countSummary() {
        return EMallsResponseEntity.ok(jobAnalyticsService.getCountSummary());
    }

    @GetMapping("/duration")
    public EMallsResponseEntity<JobDurationSummary> duration() {
        return EMallsResponseEntity.ok(jobAnalyticsService.getDurationSummary());
    }

    @GetMapping("/by-type")
    public EMallsResponseEntity<List<JobFailureRateDto>> byType() {
        return EMallsResponseEntity.ok(jobAnalyticsService.getJobsByType());
    }

    @GetMapping("/by-status")
    public EMallsResponseEntity<List<JobFailureRateDto>> byStatus() {
        return EMallsResponseEntity.ok(jobAnalyticsService.getJobsByStatus());
    }
}