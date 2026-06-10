package store.emall.backend.accounts.request.shopowner;

import store.emall.backend.accounts.city.CityMapper;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.common.phone_number.PhoneNumberMapper;
import store.emall.backend.accounts.mall.MallMapper;
import store.emall.backend.accounts.request.shop.ShopRequest;
import store.emall.backend.accounts.request.shop.ShopRequestDto;
import store.emall.backend.accounts.user.UserMapper;

import java.util.List;
import java.util.Optional;

public class ShopOwnerRequestMapper {

    private ShopOwnerRequestMapper() {}

    public static ShopOwnerRequestDto toFullDto(ShopOwnerRequest shopOwnerRequest,
                                                FileDto licenseImage,
                                                List<FileDto> shopPhotos,
                                                FileDto logoImage,
                                                FileDto profilePictureImage) {
        return Optional.ofNullable(shopOwnerRequest)
                .map(r -> ShopOwnerRequestDto.builder()
                        .id(r.getId())
                        .fullName(r.getFullName())
                        .username(r.getUsername())
                        .email(r.getEmail())
                        .phone(PhoneNumberMapper.fromPhoneString(r.getPhoneNumber()))
                        .gender(r.getGender())
                        .age(r.getAge())
                        .nationalIdNumber(r.getNationalIdNumber())
                        .profilePictureUuid(r.getProfilePictureUuid())
                        .profilePictureImage(profilePictureImage)
                        .status(r.getStatus())
                        .rejectionReason(r.getRejectionReason())
                        .createdUserId(r.getCreatedUser() != null ? r.getCreatedUser().getUserId() : null)
                        .shopRequest(toFullShopRequestDto(r.getShopRequest(), licenseImage, shopPhotos, logoImage))
                        .build())
                .orElse(null);
    }

    public static ShopRequestDto toFullShopRequestDto(ShopRequest shopRequest,
                                                      FileDto licenseImage,
                                                      List<FileDto> shopPhotos,
                                                      FileDto logoImage) {
        return Optional.ofNullable(shopRequest)
                .map(s -> ShopRequestDto.builder()
                        .id(s.getId())
                        .mallId(s.getMall() != null ? s.getMall().getMallId() : null)
                        .mall(s.getMall() != null ? MallMapper.toDto(s.getMall()) : null)
                        .name(s.getName())
                        .category(s.getCategory())
                        .description(s.getDescription())
                        .location(s.getLocation())
                        .contactInfo(s.getContactInfo())
                        .logoUuid(s.getLogoUuid())
                        .logoImage(logoImage)
                        .licenseImageUuid(s.getLicenseImageUuid())
                        .licenseImage(licenseImage)
                        .shopPhotosUuids(s.getShopPhotosUuids())
                        .shopPhotos(shopPhotos)
                        .status(s.getStatus())
                        .rejectionReason(s.getRejectionReason())
                        .createdShopId(s.getCreatedShop() != null ? s.getCreatedShop().getShopId() : null)
                        .shopOwnerRequestId(s.getShopOwnerRequest() != null ? s.getShopOwnerRequest().getId() : null)
                        .existingUserId(s.getExistingUser() != null ? s.getExistingUser().getUserId() : null)
                        .shopOwnerUser(s.getExistingUser() != null ? UserMapper.toDto(s.getExistingUser()) : null)
                        .requestedMallName(s.getRequestedMallName())
                        .requestedMallCityId(s.getRequestedMallCity() != null
                                ? s.getRequestedMallCity().getCityId() : null)
                        .requestedMallCity(s.getRequestedMallCity() != null
                                ? CityMapper.toDto(s.getRequestedMallCity()) : null)
                        .build())
                .orElse(null);
    }

    public static ShopOwnerRequestDto toSimpleDto(ShopOwnerRequest shopOwnerRequest) {
        return toFullDto(shopOwnerRequest, null, null, null, null);
    }

    public static ShopRequestDto toSimpleShopRequestDto(ShopRequest shopRequest) {
        return toFullShopRequestDto(shopRequest, null, null, null);
    }

    public static ShopOwnerRequest toEntity(ShopOwnerRequestDto dto, String encodedPassword, String phoneString) {
        return Optional.ofNullable(dto)
                .map(d -> ShopOwnerRequest.builder()
                        .fullName(d.getFullName())
                        .username(d.getUsername())
                        .email(d.getEmail())
                        .phoneNumber(phoneString)
                        .password(encodedPassword)
                        .gender(d.getGender())
                        .age(d.getAge())
                        .nationalIdNumber(d.getNationalIdNumber())
                        .profilePictureUuid(d.getProfilePictureUuid())
                        .status(ShopOwnerRequestStatus.PENDING)
                        .build())
                .orElse(null);
    }
}