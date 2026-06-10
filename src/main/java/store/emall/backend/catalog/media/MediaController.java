package store.emall.backend.catalog.media;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.util.media.MediaUsageDto;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    private final CatalogMediaService catalogMediaService;

    @GetMapping("/{mediumId}/usage")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<MediaUsageDto> mediaUsage(@PathVariable UUID mediumId) {
        return EMallsResponseEntity.ok(catalogMediaService.getMediumUsage(mediumId));
    }
}
