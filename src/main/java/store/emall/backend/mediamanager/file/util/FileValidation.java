package ps.emall.mediamanager.file.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ps.emall.mediamanager.client.accounts.AccountsClient;
import ps.emall.mediamanager.client.campaigns.CampaignsClient;
import ps.emall.mediamanager.client.catalog.CatalogClient;
import ps.emall.mediamanager.client.common.dto.MediaUsageDto;
import ps.emall.mediamanager.client.common.dto.Reference;
import ps.emall.mediamanager.common.SystemService;
import ps.emall.mediamanager.file.File;
import ps.emall.mediamanager.file.FileExceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileValidation {

    private final CatalogClient catalogClient;
    private final AccountsClient accountsClient;
    private final CampaignsClient campaignsClient;

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
        try {
            MediaUsageDto usage = catalogClient.mediaUsage(fileId).getData();
            return extractReferences(usage, SystemService.CATALOG);
        } catch (Exception e) {
            throw FileExceptions.fileInUseValidationFailed(SystemService.CATALOG);
        }
    }

    private List<Reference> getAccountsUsage(UUID fileId) {
        try {
            MediaUsageDto usage = accountsClient.mediaUsage(fileId).getData();
            return extractReferences(usage, SystemService.ACCOUNTS);
        } catch (Exception e) {
            throw FileExceptions.fileInUseValidationFailed(SystemService.ACCOUNTS);
        }
    }

    private List<Reference> getCampaignsUsage(UUID fileId) {
        try {
            MediaUsageDto usage = campaignsClient.mediaUsage(fileId).getData();
            return extractReferences(usage, SystemService.CAMPAIGNS);
        } catch (Exception e) {
            throw FileExceptions.fileInUseValidationFailed(SystemService.CAMPAIGNS);
        }
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