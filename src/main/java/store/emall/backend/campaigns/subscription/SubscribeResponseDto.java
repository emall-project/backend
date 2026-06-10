package store.emall.backend.campaigns.subscription;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeResponseDto {
    private Long subscriptionId;
    private String clientSecret;
    private String stripeCustomerId;
    private BigDecimal amount;
    private String currency;
    private String planName;
}