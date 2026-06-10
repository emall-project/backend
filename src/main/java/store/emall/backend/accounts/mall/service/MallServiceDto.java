package store.emall.backend.accounts.mall.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.validation.*;
import store.emall.backend.accounts.mall.dtos.MallBasicDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MallServiceDto {

    @Null(groups = OnCreateServiceDirectly.class, message = "mallService.serviceId.null")
    @NotNull(groups = OnUpdateServiceDirectly.class, message = "mallService.serviceId.notnull")
    @Positive(message = "mallService.serviceId.positive")
    private Long serviceId;

    @NotNull(groups = OnCreateServiceDirectly.class, message = "mallService.mall.notnull")
    @Valid
    private MallBasicDto mall;

    @NotBlank(groups = {OnCreate.class, OnCreateServiceDirectly.class}, message = "mallService.name.notblank")
    @Size(min = 2, max = 255, message = "mallService.name.size")
    private String name;

    private String description;
    private Boolean isActive;
}
