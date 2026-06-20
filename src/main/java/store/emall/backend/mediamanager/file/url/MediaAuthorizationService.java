package store.emall.backend.mediamanager.file.url;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.common.EntityType;
import store.emall.backend.mediamanager.file.File;
import store.emall.backend.mediamanager.file.visibility.FileBindingRepository;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.SecurityContextUtil;

@Service
@RequiredArgsConstructor
public class MediaAuthorizationService {

    private final FileBindingRepository fileBindingRepository;

    public boolean canAccess(File file) {
        if (file == null) {
            return false;
        }
        if (MediaVisibility.PUBLIC.equals(file.getVisibility())) {
            return true;
        }
        if (SecurityContextUtil.hasAnyAuthority(SecurityConstants.ROLE_ADMIN, SecurityConstants.ROLE_INTERNAL)) {
            return true;
        }
        Long shopId = file.getShopId();
        if (shopId != null && SecurityContextUtil.isShopOwnerOf(shopId)) {
            return true;
        }

        Long currentUserId = SecurityContextUtil.getCurrentUserIdOrNull();
        return currentUserId != null
                && fileBindingRepository.existsByFile_IdAndEntityTypeAndEntityId(
                file.getId(),
                EntityType.USER,
                currentUserId.toString()
        );
    }
}
