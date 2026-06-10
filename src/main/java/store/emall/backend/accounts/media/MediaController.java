package store.emall.backend.accounts.media;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.common.response.EMallsResponseEntity;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_INTERNAL')")
public class MediaController {
    private final AccountsMediaService accountsMediaService;

    @GetMapping("/{mediumId}/usage")
    public EMallsResponseEntity<MediaUsageDto> mediaUsage(@PathVariable UUID mediumId){
        return EMallsResponseEntity.ok(accountsMediaService.getMediumUsage(mediumId));
    }
}
