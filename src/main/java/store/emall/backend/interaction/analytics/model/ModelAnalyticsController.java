package store.emall.backend.interaction.analytics.model;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.interaction.analytics.model.dto.*;
import store.emall.backend.common.response.EMallsResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/analytics/models")
@RequiredArgsConstructor
public class ModelAnalyticsController {

    private final ModelAnalyticsService modelAnalyticsService;

    @GetMapping("/overview")
    public EMallsResponseEntity<ModelAnalyticsResponse> overview() {
        return EMallsResponseEntity.ok(modelAnalyticsService.getOverview());
    }

    @GetMapping("/usage-summary")
    public EMallsResponseEntity<ModelUsageSummary> usageSummary() {
        return EMallsResponseEntity.ok(modelAnalyticsService.getUsageSummary());
    }

    @GetMapping("/latency")
    public EMallsResponseEntity<ModelLatencyDto> latency() {
        return EMallsResponseEntity.ok(modelAnalyticsService.getLatencySummary());
    }

    @GetMapping("/failure-rate")
    public EMallsResponseEntity<List<ModelFailureRateDto>> failureRate() {
        return EMallsResponseEntity.ok(modelAnalyticsService.getFailureRates());
    }

    @GetMapping("/calls-per-hour")
    public EMallsResponseEntity<List<ModelCallsPerHourDto>> callsPerHour() {
        return EMallsResponseEntity.ok(modelAnalyticsService.getCallsPerHour());
    }
}