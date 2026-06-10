package store.emall.backend.campaigns.subscription;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequestDto {

    @NotNull(message = "subscription.request.shopId.notnull")
    @Positive(message = "subscription.request.shopId.positive")
    private Long shopId;

    @NotNull(message = "subscription.request.planId.notnull")
    @Positive(message = "subscription.request.planId.positive")
    private Long planId;

    private Boolean autoRenew = false;
}