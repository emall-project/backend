package store.emall.backend.campaigns.subscription.payment;

import java.util.Optional;

public class SubscriptionPaymentMapper {

    private SubscriptionPaymentMapper() {}

    public static SubscriptionPaymentDto toDto(SubscriptionPayment entity) {
        return Optional.ofNullable(entity)
                .map(e -> SubscriptionPaymentDto.builder()
                        .paymentId(e.getPaymentId())
                        .subscriptionId(e.getSubscription().getSubscriptionId())
                        .amount(e.getAmount())
                        .currency(e.getCurrency())
                        .paymentDate(e.getPaymentDate())
                        .paymentMethod(e.getPaymentMethod())
                        .paymentStatus(e.getPaymentStatus())
                        .transactionId(e.getTransactionId())
                        .invoiceUrl(e.getInvoiceUrl())
                        .failureReason(e.getFailureReason())
                        .build())
                .orElse(null);
    }
}