package store.emall.backend.interaction.analytics.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.interaction.analytics.model.dto.*;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelAnalyticsServiceImpl implements ModelAnalyticsService {

    private final ModelInvocationRepository modelInvocationRepository;

    @Override
    @Transactional(readOnly = true)
    public ModelUsageSummary getUsageSummary() {
        Instant from = Instant.now().minusSeconds(86400 * 30L);
        Instant to = Instant.now();

        long total = modelInvocationRepository.countByStartedAtBetween(from, to);
        long success = modelInvocationRepository.countByStatusAndStartedAtBetween(ModelInvocationStatus.SUCCESS, from, to);
        long failed = modelInvocationRepository.countByStatusAndStartedAtBetween(ModelInvocationStatus.FAILED, from, to);

        double failureRate = total == 0 ? 0.0 : ((double) failed / total) * 100.0;

        return ModelUsageSummary.builder()
                .totalCalls(total)
                .successfulCalls(success)
                .failedCalls(failed)
                .failureRate(failureRate)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ModelLatencyDto getLatencySummary() {
        Instant from = Instant.now().minusSeconds(86400 * 30L);
        Instant to = Instant.now();

        Double avg = modelInvocationRepository.averageLatency(from, to);
        Long max = modelInvocationRepository.maxLatency(from, to);

        return ModelLatencyDto.builder()
                .averageLatencyMs(avg == null ? 0.0 : avg)
                .maxLatencyMs(max == null ? 0L : max)
                .build();
    }


    @Override
    public List<ModelFailureRateDto> getFailureRates() {
        Instant from = Instant.now().minusSeconds(86400L * 30);
        Instant to = Instant.now();

        return modelInvocationRepository.failureRateByModel(from, to, ModelInvocationStatus.FAILED).stream()
                .map(this::toModelFailureRateDto)
                .toList();
    }

    @Override
    public List<ModelCallsPerHourDto> getCallsPerHour() {
        Instant from = Instant.now().minusSeconds(86400L * 7);
        Instant to = Instant.now();

        return modelInvocationRepository.callsPerHour(from, to).stream()
                .map(this::toModelCallsPerHourDto)
                .toList();
    }

    @Override
    public ModelAnalyticsResponse getOverview() {
        return ModelAnalyticsResponse.builder()
                .usageSummary(getUsageSummary())
                .latency(getLatencySummary())
                .failureRates(getFailureRates())
                .callsPerHour(getCallsPerHour())
                .build();
    }

    private ModelFailureRateDto toModelFailureRateDto(Object[] row) {
        String modelName = (String) row[0];
        long totalCalls = ((Number) row[1]).longValue();
        long failedCalls = row[2] == null ? 0L : ((Number) row[2]).longValue();

        double failureRate = totalCalls == 0 ? 0.0 : ((double) failedCalls / totalCalls) * 100.0;

        return ModelFailureRateDto.builder()
                .modelName(modelName)
                .totalCalls(totalCalls)
                .failedCalls(failedCalls)
                .failureRate(failureRate)
                .build();
    }

    private ModelCallsPerHourDto toModelCallsPerHourDto(Object[] row) {
        String hour = String.valueOf(row[0]);
        long calls = ((Number) row[1]).longValue();

        return ModelCallsPerHourDto.builder()
                .hour(hour)
                .calls(calls)
                .build();
    }
//    @Override
//    @Transactional(readOnly = true)
//    public ModelAnalyticsResponse getOverview() {
//        return ModelAnalyticsResponse.builder()
//                .usageSummary(getUsageSummary())
//                .latency(getLatencySummary())
//                .build();
//    }

}