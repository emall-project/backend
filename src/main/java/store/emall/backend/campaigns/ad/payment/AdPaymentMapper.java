package store.emall.backend.campaigns.ad.payment;

import java.util.Optional;

public class AdPaymentMapper {

    private AdPaymentMapper() {}

    public static AdPaymentDto toDto(AdPayment entity) {
        return Optional.ofNullable(entity)
                .map(e -> AdPaymentDto.builder()
                        .paymentId(e.getPaymentId())
                        .adRequestId(e.getAdRequest().getAdRequestId())
                        .amount(e.getAmount())
                        .currency(e.getCurrency())
                        .paymentDate(e.getPaymentDate())
                        .paymentMethod(e.getPaymentMethod())
                        .paymentStatus(e.getPaymentStatus())
                        .stripePaymentIntentId(e.getStripePaymentIntentId())
                        .invoiceUrl(e.getInvoiceUrl())
                        .failureReason(e.getFailureReason())
                        .build())
                .orElse(null);
    }
}