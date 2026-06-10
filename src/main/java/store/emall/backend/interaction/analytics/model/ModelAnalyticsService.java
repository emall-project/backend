package store.emall.backend.interaction.analytics.model;

import store.emall.backend.interaction.analytics.job.JobExecutionRecord;
import store.emall.backend.interaction.analytics.model.dto.*;

import java.util.List;
import java.util.Map;

public interface ModelAnalyticsService {

    ModelUsageSummary getUsageSummary();

    ModelLatencyDto getLatencySummary();

    ModelAnalyticsResponse getOverview();

    List<ModelFailureRateDto> getFailureRates();

    List<ModelCallsPerHourDto> getCallsPerHour();
}