package ps.emall.mediamanager.client.campaigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ps.emall.mediamanager.client.common.dto.MediaUsageDto;
import ps.emall.mediamanager.config.service.CampaignsFeignConfig;

import java.util.UUID;

@FeignClient(
        name = "campaigns-service",
        url = "${services.campaigns.host}:${services.campaigns.port}",
        configuration = CampaignsFeignConfig.class
)
public interface CampaignsClient {

    @GetMapping("/media/{mediumId}/usage")
    CampaignsResponse<MediaUsageDto> mediaUsage(@PathVariable("mediumId") UUID mediumId);
}