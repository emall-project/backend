package store.emall.backend.mediamanager.file.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import store.emall.backend.accounts.media.AccountsMediaService;
import store.emall.backend.campaigns.media.CampaignsMediaService;
import store.emall.backend.catalog.media.CatalogMediaService;
import store.emall.backend.common.SystemService;
import store.emall.backend.common.util.media.MediaUsageDto;
import store.emall.backend.common.util.media.Reference;
import store.emall.backend.mediamanager.file.FileExceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileValidation {

    private final CatalogMediaService catalogMediaService;
    private final AccountsMediaService accountsMediaService;
    private final CampaignsMediaService campaignsMediaService;

    public void validateFileUsage(UUID fileId) {
        List<Reference> references = new ArrayList<>();

        references.addAll(getCatalogUsage(fileId));
        references.addAll(getAccountsUsage(fileId));
        references.addAll(getCampaignsUsage(fileId));

        if (!references.isEmpty()) {
            throw FileExceptions.fileInUse(references);
        }
    }

    private List<Reference> getCatalogUsage(UUID fileId) {
        MediaUsageDto usage = catalogMediaService.getMediumUsage(fileId);
        return extractReferences(usage, SystemService.CATALOG);
    }

    private List<Reference> getAccountsUsage(UUID fileId) {
        MediaUsageDto usage = accountsMediaService.getMediumUsage(fileId);
        return extractReferences(usage, SystemService.ACCOUNTS);
    }

    private List<Reference> getCampaignsUsage(UUID fileId) {
        MediaUsageDto usage = campaignsMediaService.getMediumUsage(fileId);
        return extractReferences(usage, SystemService.CAMPAIGNS);
    }

    private List<Reference> extractReferences(MediaUsageDto usage, SystemService systemService) {
        if (usage == null || !Boolean.TRUE.equals(usage.getInUse())) {
            return List.of();
        }

        List<Reference> references = usage.getReferences() == null ? List.of() : usage.getReferences();
        for (Reference reference : references) {
            reference.setSystemService(systemService);
        }
        return references;
    }
}