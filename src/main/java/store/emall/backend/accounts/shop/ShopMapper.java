package store.emall.backend.accounts.shop;

import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.accounts.mall.MallMapper;
import store.emall.backend.accounts.user.UserBasicDto;
import store.emall.backend.accounts.user.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class ShopMapper {

    private ShopMapper() {}

    public static ShopDto toFullDto(Shop entity, FileDto logoImage, FileDto licenseImage, List<FileDto> shopPhotos) {
        return Optional.ofNullable(entity)
                .map(e -> ShopDto.builder()
                        .shopId(e.getShopId())
                        .mall(MallMapper.toBasicDto(e.getMall()))
                        .owner(UserMapper.toBasicUser(e.getOwner()))
                        .name(e.getName())
                        .category(e.getCategory())
                        .description(e.getDescription())
                        .location(e.getLocation())
                        .contactInfo(e.getContactInfo())
                        .logoUuid(e.getLogoUuid())
                        .logoImage(logoImage)
                        .licenseImageUuid(e.getLicenseImageUuid())
                        .licenseImage(licenseImage)
                        .shopPhotosUuids(e.getShopPhotosUuids())
                        .shopPhotos(shopPhotos)
                        .status(e.getStatus())
                        .folderId(e.getFolderId())
                        .adminStatus(e.getAdminStatus())
                        .build())
                .orElse(null);
    }

    public static ShopDto toDto(Shop entity) {
        return toFullDto(entity, null, null, null);
    }

    public static ShopInfoDto toShopInfoResponseDto(Shop entity) {
        return Optional.ofNullable(entity)
                .map(e -> ShopInfoDto.builder()
                        .shopId(e.getShopId())
                        .name(e.getName())
                        .ownerName(e.getOwner().getUsername())
                        .ownerPhone(e.getOwner().getPhoneNumber())
                        .ownerEmail(e.getOwner().getEmail())
                        .isActive(e.isEffectivelyActive())
                        .hasWriteAccess(e.hasWriteAccess())
                        .adminStatus(e.getAdminStatus().name())
                        .build())
                .orElse(null);
    }

    public static Shop toEntity(ShopDto dto) {
        return Optional.ofNullable(dto)
                .map(d -> Shop.builder()
                        .shopId(d.getShopId())
                        .name(d.getName())
                        .category(d.getCategory())
                        .description(d.getDescription())
                        .location(d.getLocation())
                        .contactInfo(d.getContactInfo())
                        .logoUuid(d.getLogoUuid())
                        .licenseImageUuid(d.getLicenseImageUuid())
                        .shopPhotosUuids(d.getShopPhotosUuids() != null ? d.getShopPhotosUuids() : new ArrayList<>())
                        .status(d.getStatus() != null ? d.getStatus() : ShopStatus.ACTIVE)
                        .folderId(d.getFolderId())
                        .build())
                .orElse(null);
    }

    public static Shop merge(Shop existing, ShopDto dto) {
        if (existing == null || dto == null) {
            return existing;
        }

        existing.setName(firstNonNull(dto.getName(), existing.getName()));
        existing.setCategory(firstNonNull(dto.getCategory(), existing.getCategory()));
        existing.setDescription(firstNonNull(dto.getDescription(), existing.getDescription()));
        existing.setLocation(firstNonNull(dto.getLocation(), existing.getLocation()));
        existing.setLogoUuid(firstNonNull(dto.getLogoUuid(), existing.getLogoUuid()));
        existing.setLicenseImageUuid(firstNonNull(dto.getLicenseImageUuid(), existing.getLicenseImageUuid()));

        if (dto.getShopPhotosUuids() != null) {
            existing.setShopPhotosUuids(dto.getShopPhotosUuids());
        }

        if (dto.getContactInfo() != null) {
            existing.setContactInfo(dto.getContactInfo());
        }

        existing.setStatus(firstNonNull(dto.getStatus(), existing.getStatus()));
        return existing;
    }
}