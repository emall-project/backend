package store.emall.backend.interaction.analytics.job;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.interaction.analytics.job.dto.*;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobAnalyticsServiceImpl implements JobAnalyticsService {

    private final JobExecutionRepository jobExecutionRepository;

    @Override
    public JobCountSummary getCountSummary() {
        Instant from = Instant.now().minusSeconds(86400L * 30);
        Instant to = Instant.now();

        long total = jobExecutionRepository.countByStartedAtBetween(from, to);
        long success = jobExecutionRepository.countByStatusAndStartedAtBetween(JobExecutionStatus.SUCCESS, from, to);
        long failed = jobExecutionRepository.countByStatusAndStartedAtBetween(JobExecutionStatus.FAILED, from, to);
        long skipped = jobExecutionRepository.countByStatusAndStartedAtBetween(JobExecutionStatus.SKIPPED, from, to);
        long started = jobExecutionRepository.countByStatusAndStartedAtBetween(JobExecutionStatus.STARTED, from, to);

        return JobCountSummary.builder()
                .totalJobs(total)
                .successfulJobs(success)
                .failedJobs(failed)
                .skippedJobs(skipped)
                .startedJobs(started)
                .build();
    }

    @Override
    public List<JobFailureRateDto> getJobsByType() {
        Instant from = Instant.now().minusSeconds(86400L * 30);
        Instant to = Instant.now();

        return jobExecutionRepository.countByJobType(from, to).stream()
                .map(JobAnalyticsMapper::toJobFailureRateDtoFromTypeCount)
                .toList();
    }

    @Override
    public List<JobFailureRateDto> getJobsByStatus() {
        Instant from = Instant.now().minusSeconds(86400L * 30);
        Instant to = Instant.now();

        return jobExecutionRepository.countByStatus(from, to).stream()
                .map(JobAnalyticsMapper::toJobFailureRateDtoFromStatusCount)
                .toList();
    }

    @Override
    public JobDurationSummary getDurationSummary() {
        Instant from = Instant.now().minusSeconds(86400L * 30);
        Instant to = Instant.now();

        Double avg = jobExecutionRepository.averageDuration(from, to);
        Long max = jobExecutionRepository.maxDuration(from, to);

        return JobDurationSummary.builder()
                .averageDurationMs(avg == null ? 0.0 : avg)
                .maxDurationMs(max == null ? 0L : max)
                .build();
    }

    @Override
    public JobAnalyticsResponse getOverview() {
        return JobAnalyticsResponse.builder()
                .countSummary(getCountSummary())
                .durationSummary(getDurationSummary())
                .jobsByType(getJobsByType())
                .jobsByStatus(getJobsByStatus())
                .build();
    }
}