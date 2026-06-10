package store.emall.backend.interaction.analytics.job;

import store.emall.backend.interaction.analytics.job.dto.JobFailureRateDto;

public final class JobAnalyticsMapper {

    private JobAnalyticsMapper() {
    }

    public static JobFailureRateDto toJobFailureRateDtoFromTypeCount(Object[] row) {
        return JobFailureRateDto.builder()
                .label((String) row[0])
                .count((Long) row[1])
                .build();
    }

    public static JobFailureRateDto toJobFailureRateDtoFromStatusCount(Object[] row) {
        return JobFailureRateDto.builder()
                .label(String.valueOf(row[0]))
                .count((Long) row[1])
                .build();
    }
}