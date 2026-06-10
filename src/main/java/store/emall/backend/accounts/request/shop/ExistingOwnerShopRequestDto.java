package store.emall.backend.accounts.request.shop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExistingOwnerShopRequestDto {

    @NotNull(message = "existingOwner.shopRequest.notnull")
    @Valid
    private ShopRequestDto shopRequest;

}