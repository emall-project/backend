package store.emall.backend.campaigns.offer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveDiscountsRequest {

    @NotEmpty(message = "offer.productIds.notempty")
    private List<@NotNull(message = "offer.productId.notnull")
    @Positive(message = "offer.productId.positive") Long> productIds;
}