package store.emall.backend.campaigns.subscription;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.campaigns.subscription.payment.SubscriptionPaymentDto;

import java.math.BigDecimal;
import java.util.List;

public interface ShopSubscriptionService {

    // Trial
    ShopSubscriptionDto createTrial(Long shopId, String shopEmail, String shopName);

    // Subscribe
    SubscribeResponseDto subscribe(SubscribeRequestDto dto);

    // Cancel
    void cancel(Long subscriptionId);

    // Admin reads
    PaginatedResponse<ShopSubscriptionDto> getAll(Pageable pageable, Specification<ShopSubscription> spec);
    List<ShopSubscriptionDto> getAllSubscriptions(Specification<ShopSubscription> spec);
    ShopSubscriptionDto getById(Long subscriptionId);

    // Shop owner reads
    ShopSubscriptionDto getByShopId(Long shopId);
    SubscriptionStatusDto getSubscriptionStatus(Long shopId);
    boolean hasWriteAccess(Long shopId);
    List<SubscriptionPaymentDto> getPaymentHistory(Long subscriptionId);
    List<SubscriptionPaymentDto> getPaymentHistoryForShop(Long subscriptionId);
    List<SubscriptionPaymentDto> getPaymentHistoryByShop(Long shopId);
    List<SubscriptionPaymentDto> getAllPaymentHistory();

    // Stripe Webhook Handlers
    void handlePaymentSuccess(String stripeSubscriptionId, String transactionId,
                              String invoiceUrl, BigDecimal amount, String currency);
    void handlePaymentFailed(String stripeSubscriptionId, String failureReason);
    void handleSubscriptionCancelled(String stripeSubscriptionId);

    // Scheduler
    void expireTrials();
    void expireSubscriptions();
    void suspendGracePeriodExpired();
    void sendTrialEndingSoonNotifications();
    void sendSubscriptionExpiringSoonNotifications();

}