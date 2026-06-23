package store.emall.backend.accounts.shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.campaigns.subscription.ShopSubscriptionService;
import store.emall.backend.catalog.brand.BrandDto;
import store.emall.backend.catalog.brand.BrandService;
import store.emall.backend.common.audience.AgeGroup;
import store.emall.backend.common.audience.TargetedAudience;
import store.emall.backend.common.EntityType;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.accounts.mall.Mall;
import store.emall.backend.accounts.mall.MallExceptions;
import store.emall.backend.accounts.mall.MallRepository;
import store.emall.backend.common.scope.ManagedByType;
import store.emall.backend.common.scope.ScopeType;
import store.emall.backend.mediamanager.file.service.FileService;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.dto.FileTransferRequest;
import store.emall.backend.mediamanager.file.visibility.MediaVisibilityService;
import store.emall.backend.mediamanager.folder.FolderService;
import store.emall.backend.mediamanager.folder.dto.FolderDto;
import store.emall.backend.security.SecurityContextUtil;
import store.emall.backend.accounts.user.User;
import store.emall.backend.accounts.user.UserExceptions;
import store.emall.backend.accounts.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final MallRepository mallRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final FolderService folderService;
    private final BrandService brandService;
    private final ObjectProvider<ShopSubscriptionService> shopSubscriptionServiceProvider;
    private final MediaVisibilityService mediaVisibilityService;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ShopDto> getAll(Pageable pageable, Specification<Shop> spec) {
        Page<Shop> page = shopRepository.findAll(spec, pageable);
        Map<UUID, FileDto> mediaById = fetchMediaMapSafely(page.getContent());
        Page<ShopDto> dtoPage = page.map(shop -> toDtoWithMedia(shop, mediaById));
        return PaginatedResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDto> getAllShops(Specification<Shop> spec) {
        List<Shop> shops = spec == null ? shopRepository.findAll() : shopRepository.findAll(spec);
        return toDtosWithMedia(shops);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopDto getById(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        return toDtoWithMedia(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopInfoDto getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(ShopExceptions::shopNotFound);

        return ShopMapper.toShopInfoResponseDto(shop);
    }

    @Override
    @Transactional
    public ShopDto create(ShopDto shopDto) {
        // Validate Mall
        if (shopDto.getMall() == null || shopDto.getMall().getMallId() == null) {
            throw new IllegalArgumentException("Mall ID is required");
        }
        Mall mall = mallRepository.findById(shopDto.getMall().getMallId())
                .orElseThrow(MallExceptions::mallNotFound);

        // Validate Owner
        if (shopDto.getOwner() == null || shopDto.getOwner().getUserId() == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }
        User owner = userRepository.findById(shopDto.getOwner().getUserId())
                .orElseThrow(UserExceptions::userNotFound);

        // Validate shop name uniqueness in mall
        if (shopRepository.existsByNameAndMall_MallId(shopDto.getName(), mall.getMallId())) {
            throw ShopExceptions.shopNameExistsInMall();
        }

        // Validate and fetch media files from media-manager
        FileDto logoImage = null;
        if (shopDto.getLogoUuid() != null) {
            logoImage = fileService.getAndValidateImage(shopDto.getLogoUuid(), "LogoUuid");
        }

        FileDto licenseImage = fileService.getAndValidateImage(shopDto.getLicenseImageUuid(), "LicenseImageUuid");
        List<FileDto> shopPhotos = fileService.getAndValidateImages(shopDto.getShopPhotosUuids(), "ShopPhotosUuids");

        Shop shop = ShopMapper.toEntity(shopDto);
        shop.setMall(mall);
        shop.setOwner(owner);

        Shop savedShop = shopRepository.save(shop);
        createShopFolder(savedShop);
        createBrandForShopSafely(savedShop);
        triggerTrial(savedShop, owner);

        moveFileSafely(savedShop.getLicenseImageUuid(), savedShop.getFolderId(),
                "licenseImage", savedShop.getShopId());

        if (savedShop.getShopPhotosUuids() != null) {
            for (int i = 0; i < savedShop.getShopPhotosUuids().size(); i++) {
                moveFileSafely(savedShop.getShopPhotosUuids().get(i), savedShop.getFolderId(),
                        "shopPhoto[" + i + "]", savedShop.getShopId());
            }
        }

        if (savedShop.getLogoUuid() != null) {
            moveFileSafely(savedShop.getLogoUuid(), savedShop.getFolderId(),
                    "logo", savedShop.getShopId());
        }
        syncShopMediaBindings(savedShop);

        log.info("Shop created: shopId={}, name={}, mallId={}",
                savedShop.getShopId(), savedShop.getName(), mall.getMallId());

        return ShopMapper.toFullDto(savedShop, logoImage, licenseImage, shopPhotos);
    }

    @Override
    @Transactional
    public ShopDto update(ShopDto shopDto) {
        Shop existing = shopRepository.findById(shopDto.getShopId())
                .orElseThrow(ShopExceptions::shopNotFound);

        boolean isAdmin = SecurityContextUtil.isAdmin();
        if (!isAdmin) {
            if (shopDto.getMall() != null && shopDto.getMall().getMallId() != null
                    && !shopDto.getMall().getMallId().equals(existing.getMall().getMallId())) {
                throw ShopExceptions.cannotChangeMall();
            }
            if (shopDto.getOwner() != null && shopDto.getOwner().getUserId() != null
                    && !shopDto.getOwner().getUserId().equals(existing.getOwner().getUserId())) {
                throw ShopExceptions.cannotChangeOwner();
            }
        }

        // Handle mall change if provided
        if (shopDto.getMall() != null && shopDto.getMall().getMallId() != null) {
            Long newMallId = shopDto.getMall().getMallId();
            Long currentMallId = existing.getMall().getMallId();
            if (!newMallId.equals(currentMallId)) {
                Mall newMall = mallRepository.findById(newMallId)
                        .orElseThrow(MallExceptions::mallNotFound);

                // Re-check name uniqueness in new mall
                if (shopRepository.existsByNameAndMall_MallIdAndShopIdNot(
                        shopDto.getName(), newMallId, shopDto.getShopId())) {
                    throw ShopExceptions.shopNameExistsInMall();
                }

                existing.setMall(newMall);
            }
        }

        // Handle owner change if provided
        if (shopDto.getOwner() != null && shopDto.getOwner().getUserId() != null) {
            Long newOwnerId = shopDto.getOwner().getUserId();
            Long currentOwnerId = existing.getOwner().getUserId();
            if (!newOwnerId.equals(currentOwnerId)) {
                User newOwner = userRepository.findById(newOwnerId)
                        .orElseThrow(UserExceptions::userNotFound);
                existing.setOwner(newOwner);
            }
        }

        // Handle name change within same mall
        if (shopDto.getName() != null && !shopDto.getName().equals(existing.getName())) {
            if (shopRepository.existsByNameAndMall_MallIdAndShopIdNot(
                    shopDto.getName(), existing.getMall().getMallId(), shopDto.getShopId())) {
                throw ShopExceptions.shopNameExistsInMall();
            }
        }

        // Validate new media files if provided
        UUID newLogoUuid = (shopDto.getLogoUuid() != null
                && !shopDto.getLogoUuid().equals(existing.getLogoUuid()))
                ? shopDto.getLogoUuid() : null;

        UUID newLicenseUuid = (shopDto.getLicenseImageUuid() != null
                && !shopDto.getLicenseImageUuid().equals(existing.getLicenseImageUuid()))
                ? shopDto.getLicenseImageUuid() : null;

        List<UUID> existingPhotoUuids = existing.getShopPhotosUuids() != null
                ? existing.getShopPhotosUuids() : Collections.emptyList();

        List<UUID> newPhotoUuids = Collections.emptyList();
        if (shopDto.getShopPhotosUuids() != null && !shopDto.getShopPhotosUuids().isEmpty()) {
            newPhotoUuids = shopDto.getShopPhotosUuids().stream()
                    .filter(uuid -> !existingPhotoUuids.contains(uuid))
                    .collect(Collectors.toList());
        }

        if (newLogoUuid != null) {
            fileService.getAndValidateImage(newLogoUuid, "LogoUuid");
        }
        if (newLicenseUuid != null) {
            fileService.getAndValidateImage(newLicenseUuid, "LicenseImageUuid");
        }
        if (!newPhotoUuids.isEmpty()) {
            fileService.getAndValidateImages(newPhotoUuids, "ShopPhotosUuids");
        }

        ShopMapper.merge(existing, shopDto);
        Shop savedShop = shopRepository.save(existing);

        if (newLicenseUuid != null) {
            moveFileSafely(newLicenseUuid, savedShop.getFolderId(),
                    "licenseImage", savedShop.getShopId());
        }
        for (int i = 0; i < newPhotoUuids.size(); i++) {
            moveFileSafely(newPhotoUuids.get(i), savedShop.getFolderId(),
                    "shopPhoto[" + i + "]", savedShop.getShopId());
        }
        if (newLogoUuid != null) {
            moveFileSafely(newLogoUuid, savedShop.getFolderId(),
                    "logo", savedShop.getShopId());
        }
        syncShopMediaBindings(savedShop);

        log.info("Shop updated: shopId={}", savedShop.getShopId());
        return toDtoWithMedia(savedShop);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        mediaVisibilityService.removeEntityBindings(EntityType.SHOP, id);
        shopRepository.delete(shop);

        log.info("Shop deleted: shopId={}", id);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        shop.setStatus(ShopStatus.ACTIVE);
        shopRepository.save(shop);
        // Note: if adminStatus is BLOCKED or MAINTENANCE,
        // isEffectivelyActive() will still return false — admin decision wins visibility
        log.info("Shop subscription-activated: shopId={}, adminStatus={}", id, shop.getAdminStatus());
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        shop.setStatus(ShopStatus.INACTIVE);
        shopRepository.save(shop);
        log.info("Shop subscription-deactivated: shopId={}", id);
    }

    @Override
    @Transactional
    public void setMaintenance(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        shop.setAdminStatus(ShopAdminStatus.MAINTENANCE);
        shopRepository.save(shop);
        log.info("Shop set to MAINTENANCE by admin: shopId={}", id);
    }

    @Override
    @Transactional
    public void adminBlock(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        shop.setAdminStatus(ShopAdminStatus.BLOCKED);
        shopRepository.save(shop);
        log.info("Shop BLOCKED by admin: shopId={}", id);
    }

    @Override
    @Transactional
    public void adminClearBlock(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
        shop.setAdminStatus(ShopAdminStatus.NONE);
        shopRepository.save(shop);
        log.info("Admin override cleared for shopId={}, subscription now controls visibility", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDto> getShopsByMall(Long mallId) {
        return toDtosWithMedia(shopRepository.findByMall_MallId(mallId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDto> getActiveShopsByMall(Long mallId) {
        return toDtosWithMedia(shopRepository.findByMall_MallIdAndStatusAndAdminStatus(mallId, ShopStatus.ACTIVE, ShopAdminStatus.NONE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDto> getActiveShops() {
        return toDtosWithMedia(shopRepository.findByStatusAndAdminStatus(ShopStatus.ACTIVE, ShopAdminStatus.NONE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopDto> getShopsByOwner(Long ownerId) {
        return toDtosWithMedia(shopRepository.findByOwner_UserId(ownerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Shop getShopEntityById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(ShopExceptions::shopNotFound);
    }

    private ShopDto toDtoWithMedia(Shop shop) {
        return toDtoWithMedia(shop, fetchMediaMapSafely(Collections.singletonList(shop)));
    }

    private List<ShopDto> toDtosWithMedia(List<Shop> shops) {
        if (shops == null || shops.isEmpty()) {
            return Collections.emptyList();
        }
        Map<UUID, FileDto> mediaById = fetchMediaMapSafely(shops);
        return shops.stream()
                .map(shop -> toDtoWithMedia(shop, mediaById))
                .collect(Collectors.toList());
    }

    private ShopDto toDtoWithMedia(Shop shop, Map<UUID, FileDto> mediaById) {
        FileDto logoImage = mediaById.get(shop.getLogoUuid());
        FileDto licenseImage = mediaById.get(shop.getLicenseImageUuid());
        List<FileDto> shopPhotos = resolveFiles(shop.getShopPhotosUuids(), mediaById);
        return ShopMapper.toFullDto(shop, logoImage, licenseImage, shopPhotos);
    }

    private Map<UUID, FileDto> fetchMediaMapSafely(List<Shop> shops) {
        Set<UUID> mediaIds = collectMediaIds(shops);
        if (mediaIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<FileDto> files = fileService.getByIds(new ArrayList<>(mediaIds));
        return files.stream()
                .filter(file -> file != null && file.getId() != null)
                .collect(Collectors.toMap(FileDto::getId, Function.identity(), (left, right) -> left));
    }

    private Set<UUID> collectMediaIds(List<Shop> shops) {
        Set<UUID> mediaIds = new LinkedHashSet<>();
        if (shops == null || shops.isEmpty()) {
            return mediaIds;
        }
        for (Shop shop : shops) {
            if (shop == null) {
                continue;
            }
            if (shop.getLogoUuid() != null) {
                mediaIds.add(shop.getLogoUuid());
            }
            if (shop.getLicenseImageUuid() != null) {
                mediaIds.add(shop.getLicenseImageUuid());
            }
            if (shop.getShopPhotosUuids() != null) {
                shop.getShopPhotosUuids().stream()
                        .filter(Objects::nonNull)
                        .forEach(mediaIds::add);
            }
        }
        return mediaIds;
    }

    private List<FileDto> resolveFiles(List<UUID> uuids, Map<UUID, FileDto> mediaById) {
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }
        return uuids.stream()
                .map(mediaById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void createShopFolder(Shop shop) {
        if (shop.getFolderId() != null) {
            return; // already has a folder
        }
        String folderName = "shop_" + shop.getShopId();
        FolderDto folderDto = FolderDto.builder()
                .name(folderName)
                .parentId(null)
                .shopId(shop.getShopId())
                .build();
        FolderDto saved = folderService.create(folderDto);
        Long newFolderId = saved.getId();
        shop.setFolderId(newFolderId);
        shopRepository.save(shop);
    }

    private void syncShopMediaBindings(Shop shop) {
        if (shop == null || shop.getShopId() == null) {
            return;
        }
        mediaVisibilityService.syncPublicBindings(
                EntityType.SHOP,
                shop.getShopId(),
                "logo",
                shop.getLogoUuid() == null ? List.of() : List.of(shop.getLogoUuid())
        );
        mediaVisibilityService.syncPublicBindings(
                EntityType.SHOP,
                shop.getShopId(),
                "photos",
                shop.getShopPhotosUuids() == null ? List.of() : shop.getShopPhotosUuids()
        );
        mediaVisibilityService.syncPrivateBindings(
                EntityType.SHOP,
                shop.getShopId(),
                "license",
                shop.getLicenseImageUuid() == null ? List.of() : List.of(shop.getLicenseImageUuid())
        );
    }

    private void moveFileSafely(UUID fileUuid, Long targetFolderId, String fieldName, Long shopId) {
        if (fileUuid == null || targetFolderId == null) {
            return;
        }
        FileTransferRequest fileTransferRequest = FileTransferRequest.builder()
                .id(fileUuid)
                .newFolderId(targetFolderId)
                .newShopId(shopId)
                .newScope(ScopeType.SHOP)
                .newManagedBy(ManagedByType.SYSTEM)
                .build();

        fileService.transfer(fileTransferRequest);
    }

    private void createBrandForShopSafely(Shop shop) {

        UUID imageId = shop.getLogoUuid() != null
                ? shop.getLogoUuid()
                : shop.getShopPhotosUuids().getFirst();

        String slug = shop.getName()
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        if (slug.length() < 5) {
            slug = slug + "-shop";
        }

        BrandDto brandDto = BrandDto.builder()
                .name(shop.getName())
                .slug(slug)
                .imageId(imageId)
                .targetedAudience(TargetedAudience.ALL)
                .ageGroup(AgeGroup.ALL)
                .build();

        brandService.create(brandDto);
    }

    private void triggerTrial(Shop savedShop, User owner) {
        shopSubscriptionServiceProvider.getObject().createTrial(
                savedShop.getShopId(),
                owner.getEmail() != null ? owner.getEmail() : "",
                savedShop.getName()
        );
        // Non-fatal: shop is created successfully, subscription will be created manually if needed
    }

}
