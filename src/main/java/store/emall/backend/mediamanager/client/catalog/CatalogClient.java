package ps.emall.mediamanager.client.catalog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ps.emall.mediamanager.client.common.dto.MediaUsageDto;
import ps.emall.mediamanager.config.service.CatalogFeignConfig;

import java.util.UUID;

@FeignClient(
        name = "catalog-service",
        url = "${services.catalog.host}:${services.catalog.port}",
        configuration = CatalogFeignConfig.class
)
public interface CatalogClient {

    @GetMapping("/media/{mediumId}/usage")
    CatalogResponse<MediaUsageDto> mediaUsage(@PathVariable("mediumId") UUID mediumId);
}
