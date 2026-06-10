package store.emall.backend.campaigns.ad.request;

import store.emall.backend.campaigns.ad.template.AdTemplate;
import store.emall.backend.campaigns.ad.template.AdTemplateMapper;
import store.emall.backend.accounts.shop.ShopInfoDto;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class AdRequestMapper {

    private AdRequestMapper() {}

    public static AdRequestDto toFullDto(AdRequest adRequest, FileDto adRequestImage, ShopInfoDto shopInfo) {
        return Optional.ofNullable(adRequest)
                .map(e -> AdRequestDto.builder()
                        .adRequestId(e.getAdRequestId())
                        .templateId(e.getTemplate() != null ? e.getTemplate().getAdTemplateId() : null)
                        .template(e.getTemplate() != null ? AdTemplateMapper.toDto(e.getTemplate()) : null)
                        .shopId(e.getShopId())
                        .shop(shopInfo)
                        .title(e.getTitle())
                        .startDate(e.getStartDate())
                        .endDate(e.getEndDate())
                        .totalPrice(e.getTotalPrice())
                        .status(e.getStatus())
                        .paymentStatus(e.getPaymentStatus())
                        .paidAt(e.getPaidAt())
                        .rejectionReason(e.getRejectionReason())
                        .isDisplayed(e.getIsDisplayed())
                        .paymentReminderSent(e.getPaymentReminderSent())
                        .adRequestImageUuid(e.getAdRequestImageUuid())
                        .adRequestImage(adRequestImage)
                        .build())
                .orElse(null);
    }

    private static AdRequestDto toDto(AdRequest adRequest) {
        return toFullDto(adRequest, null, null);
    }

    public static AdRequest toEntity(AdRequestDto dto, AdTemplate template, java.math.BigDecimal totalPrice) {
        return Optional.ofNullable(dto)
                .map(d -> AdRequest.builder()
                        .adRequestId(d.getAdRequestId())
                        .template(template)
                        .shopId(d.getShopId())
                        .title(d.getTitle())
                        .startDate(d.getStartDate())
                        .endDate(d.getEndDate())
                        .totalPrice(totalPrice)
                        .status(d.getStatus() != null ? d.getStatus() : AdRequestStatus.PENDING)
                        .paymentStatus(AdPaymentStatus.UNPAID)
                        .isDisplayed(false)
                        .paymentReminderSent(false)
                        .adRequestImageUuid(d.getAdRequestImageUuid())
                        .build())
                .orElse(null);
    }

    public static AdRequest merge(AdRequest existing, AdRequestDto dto) {
        if (existing == null || dto == null) {
            return existing;
        }

        existing.setTitle(firstNonNull(dto.getTitle(), existing.getTitle()));
        existing.setAdRequestImageUuid(firstNonNull(dto.getAdRequestImageUuid(), existing.getAdRequestImageUuid()));

        return existing;
    }
}