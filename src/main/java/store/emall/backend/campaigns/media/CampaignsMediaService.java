package store.emall.backend.campaigns.media;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.campaigns.ad.request.AdRequest;
import store.emall.backend.campaigns.ad.request.AdRequestRepository;
import store.emall.backend.common.EntityType;
import store.emall.backend.common.util.media.MediaUsageDto;
import store.emall.backend.common.util.media.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CampaignsMediaService {
    private final AdRequestRepository adRequestRepository;

    public MediaUsageDto getMediumUsage(UUID mediumId) {
        List<AdRequest> adRequests = adRequestRepository.findByAdRequestImageUuid(mediumId);

        List<Reference> references = new ArrayList<>();
        boolean inUse = false;

        if (adRequests.size() > 0) {
            inUse = true;
            for (AdRequest adRequest : adRequests) {
                Reference reference = Reference.builder()
                        .entityType(EntityType.AD_REQUEST)
                        .entityId(adRequest.getAdRequestId())
                        .entityName(adRequest.getTitle())
                        .build();
                references.add(reference);
            }
        }


        return MediaUsageDto.builder()
                .inUse(inUse)
                .references(references)
                .build();
    }

}
