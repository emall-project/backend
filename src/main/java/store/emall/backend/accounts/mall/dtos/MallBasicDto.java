package store.emall.backend.accounts.mall.dtos;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.accounts.mall.MallStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MallBasicDto {

    @NotNull(message = "basic_mall.mallId.notnull")
    private Long mallId;
    private String name;
    private MallStatus status;

}
