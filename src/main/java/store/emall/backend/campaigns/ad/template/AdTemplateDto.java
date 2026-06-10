package store.emall.backend.campaigns.ad.template;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdTemplateDto {

    @Null(groups = OnCreate.class, message = "adTemplate.adTemplateId.null")
    @NotNull(groups = OnUpdate.class, message = "adTemplate.adTemplateId.notnull")
    @Positive(message = "adTemplate.adTemplateId.positive")
    private Long adTemplateId;

    @NotBlank(groups = OnCreate.class, message = "adTemplate.name.notblank")
    @Size(min = 2, max = 255, message = "adTemplate.name.size")
    private String name;

    private String description;

    @NotBlank(groups = OnCreate.class, message = "adTemplate.position.notblank")
    @Size(max = 255, message = "adTemplate.position.size")
    private String position;

    @NotNull(groups = OnCreate.class, message = "adTemplate.imageRatio.notnull")
    private ImageRatio imageRatio;

    @NotNull(groups = OnCreate.class, message = "adTemplate.price.notnull")
    @DecimalMin(value = "0.00", message = "adTemplate.price.min")
    private BigDecimal pricePerHour;

    private AdTemplateStatus status;

    private Long activeRequestCount;
}
