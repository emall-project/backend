package store.emall.backend.campaigns.ad.request;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.campaigns.ad.template.AdTemplateDto;
import store.emall.backend.accounts.shop.ShopInfoDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdRequestDto {

    @Null(groups = OnCreate.class, message = "adRequest.adRequestId.null")
    @NotNull(groups = OnUpdate.class, message = "adRequest.adRequestId.notnull")
    @Positive(message = "adRequest.adRequestId.positive")
    private Long adRequestId;

    @NotNull(groups = OnCreate.class, message = "adRequest.templateId.notnull")
    @Positive(message = "adRequest.templateId.positive")
    private Long templateId;

    private AdTemplateDto template;

    @NotNull(groups = OnCreate.class, message = "adRequest.shopId.notnull")
    @Positive(message = "adRequest.shopId.positive")
    private Long shopId;

    private ShopInfoDto shop;

    @NotBlank(groups = OnCreate.class, message = "adRequest.title.notblank")
    @Size(min = 2, max = 255, message = "adRequest.title.size")
    private String title;

    @NotNull(groups = OnCreate.class, message = "adRequest.imageUrl.notblank")
    private UUID adRequestImageUuid;

    private FileDto adRequestImage;

    @NotNull(groups = OnCreate.class, message = "adRequest.startDate.notnull")
    @FutureOrPresent(groups = OnCreate.class, message = "adRequest.startDate.futureOrPresent")
    private LocalDateTime startDate;

    @NotNull(groups = OnCreate.class, message = "adRequest.endDate.notnull")
    @Future(groups = OnCreate.class, message = "adRequest.endDate.future")
    private LocalDateTime endDate;

    private AdRequestStatus status;

    private AdPaymentStatus paymentStatus;

    private LocalDateTime paidAt;

    private String rejectionReason;

    private Boolean isDisplayed;

    private Boolean paymentReminderSent;

    private BigDecimal totalPrice;
}
