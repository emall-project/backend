package store.emall.backend.campaigns.ad.request;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdPaymentResponseDto {
    private Long adRequestId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
    private String adTitle;
}