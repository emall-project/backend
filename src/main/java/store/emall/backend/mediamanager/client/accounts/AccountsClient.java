package ps.emall.mediamanager.client.accounts;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ps.emall.mediamanager.client.common.dto.MediaUsageDto;
import ps.emall.mediamanager.config.service.AccountsFeignConfig;

import java.util.UUID;

@FeignClient(
        name = "accounts-service",
        url = "${services.accounts.host}:${services.accounts.port}",
        configuration = AccountsFeignConfig.class
)
public interface AccountsClient {

    @GetMapping("/media/{mediumId}/usage")
    AccountsResponse<MediaUsageDto> mediaUsage(@PathVariable("mediumId")UUID mediumId);
}