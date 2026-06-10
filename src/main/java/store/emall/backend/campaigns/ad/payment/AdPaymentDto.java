package store.emall.backend.campaigns.ad.payment;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdPaymentDto {
    private Long paymentId;
    private Long adRequestId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paymentDate;
    private AdPaymentMethod paymentMethod;
    private AdPaymentRecordStatus paymentStatus;
    private String stripePaymentIntentId;
    private String invoiceUrl;
    private String failureReason;
}