package store.emall.backend.accounts.city;

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
public class CityDto {

    @Null(groups = OnCreate.class, message = "city.cityId.null")
    @NotNull(groups = OnUpdate.class, message = "city.cityId.notnull")
    @Positive(message = "city.cityId.positive")
    private Long cityId;

    @NotBlank(groups = OnCreate.class, message = "city.name.notblank")
    @Size(min = 2, max = 100, message = "city.name.size")
    private String name;

    @NotNull(groups = OnCreate.class, message = "city.baseFee.notnull")
    @DecimalMin(value = "0.0", inclusive = true, message = "city.baseFee.min")
    @Digits(integer = 10, fraction = 2, message = "city.baseFee.digits")
    private BigDecimal baseFee;

    private Boolean isActive;
}
