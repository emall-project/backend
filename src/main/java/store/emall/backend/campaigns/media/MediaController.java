package store.emall.backend.campaigns.media;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.common.response.EMallsResponseEntity;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    private final CampaignsMediaService campaignsMediaService;

    @GetMapping("/{mediumId}/usage")
    public EMallsResponseEntity<MediaUsageDto> mediaUsage(@PathVariable UUID mediumId){
        return EMallsResponseEntity.ok(campaignsMediaService.getMediumUsage(mediumId));
    }
}
