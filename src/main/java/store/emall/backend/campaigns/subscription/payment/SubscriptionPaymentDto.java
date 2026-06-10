package store.emall.backend.campaigns.subscription.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPaymentDto {
    private Long paymentId;
    private Long subscriptionId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paymentDate;
    private SubscriptionPaymentMethod paymentMethod;
    private SubscriptionPaymentStatus paymentStatus;
    private String transactionId;
    private String invoiceUrl;
    private String failureReason;
}