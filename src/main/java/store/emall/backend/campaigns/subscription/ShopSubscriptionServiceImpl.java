package store.emall.backend.campaigns.subscription;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.accounts.shop.ShopInfoDto;
import store.emall.backend.accounts.shop.ShopService;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.campaigns.security.SecurityContextUtil;
import store.emall.backend.campaigns.subscription.payment.*;
import store.emall.backend.campaigns.subscription.plan.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopSubscriptionServiceImpl implements ShopSubscriptionService {

    private final ShopSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPaymentRepository paymentRepository;
    private final ShopService shopService;

    //  TRIAL

    @Override
    @Transactional
    public ShopSubscriptionDto createTrial(Long shopId, String shopEmail, String shopName) {

        // Idempotent — return existing if already created
        if (subscriptionRepository.existsByShopId(shopId)) {
            log.warn("Trial already exists for shopId={}, returning existing.", shopId);
            return subscriptionRepository.findByShopId(shopId)
                    .map(ShopSubscriptionMapper::toDto)
                    .orElseThrow(SubscriptionExceptions::subscriptionNotFound);
        }

        // Create Stripe Customer upfront (lazy fallback in subscribe() if this fails)
        String stripeCustomerId = null;
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(shopEmail != null ? shopEmail : "")
                    .setName(shopName)
                    .build();
            Customer customer = Customer.create(params);
            stripeCustomerId = customer.getId();
            log.info("Stripe customer created for shopId={}, customerId={}", shopId, stripeCustomerId);
        } catch (StripeException e) {
            log.warn("Could not create Stripe customer for shopId={}: {}", shopId, e.getMessage());
        }

        LocalDate today = LocalDate.now();
        ShopSubscription subscription = ShopSubscription.builder()
                .shopId(shopId)
                .status(SubscriptionStatus.TRIAL)
                .startDate(today)
                .trialEndDate(today.plusMonths(3))
                .autoRenew(false)
                .paymentFailureCount(0)
                .stripeCustomerId(stripeCustomerId)
                .build();

        ShopSubscription saved = subscriptionRepository.save(subscription);
        log.info("Trial created for shopId={}, trialEndDate={}", shopId, saved.getTrialEndDate());
        return ShopSubscriptionMapper.toDto(saved);
    }

    //  SUBSCRIBE

    @Override
    @Transactional
    public SubscribeResponseDto subscribe(SubscribeRequestDto dto) {

        // Ownership check
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(dto.getShopId())) {
                throw SubscriptionExceptions.shopNotFound();
            }
        }

        ShopSubscription subscription = subscriptionRepository.findByShopId(dto.getShopId())
                .orElseThrow(SubscriptionExceptions::subscriptionNotFound);

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            throw SubscriptionExceptions.subscriptionAlreadyActive();
        }

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw SubscriptionExceptions.subscriptionCancelled();
        }

        SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(SubscriptionExceptions::planNotFound);
        if (Boolean.FALSE.equals(plan.getIsActive())) {
            throw SubscriptionExceptions.planNotFound();
        }

        if (subscription.getStripeCustomerId() == null) {
            subscription.setStripeCustomerId(createStripeCustomerForShop(dto.getShopId()));
        }

        try {
            SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                    .setCustomer(subscription.getStripeCustomerId())
                    .addItem(SubscriptionCreateParams.Item.builder()
                            .setPrice(plan.getStripePriceId())
                            .build())
                    .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                    .addExpand("latest_invoice.payment_intent")
                    .build();

            Subscription stripeSub = Subscription.create(params);

            String clientSecret = stripeSub
                    .getLatestInvoiceObject()
                    .getPaymentIntentObject()
                    .getClientSecret();

            if (subscription.getStatus() == SubscriptionStatus.TRIAL) {
                subscription.setTrialEndDate(LocalDate.now());
                log.info("Shop {} subscribing early — trial ended today", dto.getShopId());
            }

            if (subscription.getStatus() == SubscriptionStatus.SUSPENDED) {
                subscription.setPaymentFailureCount(0);
                subscription.setSuspendedAt(null);
            }

            subscription.setStripeSubscriptionId(stripeSub.getId());
            subscription.setPlan(plan);
            subscription.setPricePaid(plan.getPrice());
            subscription.setAutoRenew(Boolean.TRUE.equals(dto.getAutoRenew()));
            subscriptionRepository.save(subscription);

            log.info("Stripe subscription initiated for shopId={}, stripeSubId={}, plan={}",
                    dto.getShopId(), stripeSub.getId(), plan.getPlanType());

            return SubscribeResponseDto.builder()
                    .subscriptionId(subscription.getSubscriptionId())
                    .clientSecret(clientSecret)
                    .stripeCustomerId(subscription.getStripeCustomerId())
                    .amount(plan.getPrice())
                    .currency(plan.getCurrency())
                    .planName(plan.getName())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe error for shopId={}: {}", dto.getShopId(), e.getMessage());
            throw SubscriptionExceptions.stripeError();
        }
    }

    //  CANCEL

    @Override
    @Transactional
    public void cancel(Long subscriptionId) {
        ShopSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(SubscriptionExceptions::subscriptionNotFound);

        if (subscription.getStripeSubscriptionId() != null) {
            try {
                Subscription stripeSub = Subscription.retrieve(subscription.getStripeSubscriptionId());
                stripeSub.cancel();
                log.info("Stripe subscription cancelled for subscriptionId={}", subscriptionId);
            } catch (StripeException e) {
                log.warn("Could not cancel Stripe subscription for id={}: {}", subscriptionId, e.getMessage());
            }
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscriptionRepository.save(subscription);

        deactivateShopSafely(subscription.getShopId());

        log.info("Subscription CANCELLED for shopId={}", subscription.getShopId());
        notifyShopOwner(subscription.getShopId(),
                "Your subscription has been cancelled. Your shop is now inactive.");
    }

    //  READ OPERATIONS

    //  ADMIN READ OPERATIONS

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ShopSubscriptionDto> getAll(Pageable pageable, Specification<ShopSubscription> spec) {
        Page<ShopSubscriptionDto> page = subscriptionRepository.findAll(spec, pageable)
                .map(ShopSubscriptionMapper::toDto);
        return PaginatedResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopSubscriptionDto> getAllSubscriptions(Specification<ShopSubscription> spec) {
        List<ShopSubscription> subscriptions = (spec == null)
                ? subscriptionRepository.findAll()
                : subscriptionRepository.findAll(spec);
        return subscriptions.stream().map(ShopSubscriptionMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShopSubscriptionDto getById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .map(ShopSubscriptionMapper::toDto)
                .orElseThrow(SubscriptionExceptions::subscriptionNotFound);
    }

    //  SHOP OWNER READ OPERATIONS

    @Override
    @Transactional(readOnly = true)
    public ShopSubscriptionDto getByShopId(Long shopId) {
        return subscriptionRepository.findByShopId(shopId)
                .map(ShopSubscriptionMapper::toDto)
                .orElseThrow(SubscriptionExceptions::subscriptionNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionStatusDto getSubscriptionStatus(Long shopId) {
        ShopSubscription sub = subscriptionRepository.findByShopId(shopId)
                .orElseThrow(SubscriptionExceptions::subscriptionNotFound);

        return SubscriptionStatusDto.builder()
                .status(sub.getStatus())
                .trialEndDate(sub.getTrialEndDate())
                .endDate(sub.getEndDate())
                .suspendedAt(sub.getSuspendedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasWriteAccess(Long shopId) {
        return subscriptionRepository.findByShopId(shopId)
                .map(sub -> switch (sub.getStatus()) {
                    case TRIAL, ACTIVE -> true;
                    case SUSPENDED -> sub.getSuspendedAt() != null
                            && sub.getSuspendedAt().isAfter(LocalDateTime.now().minusDays(3));
                    case EXPIRED, CANCELLED -> false;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPaymentDto> getPaymentHistory(Long subscriptionId) {
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw SubscriptionExceptions.subscriptionNotFound();
        }
        return paymentRepository
                .findBySubscription_SubscriptionIdOrderByPaymentDateDesc(subscriptionId)
                .stream()
                .map(SubscriptionPaymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPaymentDto> getPaymentHistoryForShop(Long subscriptionId) {
        ShopSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(SubscriptionExceptions::subscriptionNotFound);

        // Shop owner must own this subscription's shop
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(subscription.getShopId())) {
                throw SubscriptionExceptions.subscriptionNotFound();
            }
        }

        return paymentRepository
                .findBySubscription_SubscriptionIdOrderByPaymentDateDesc(subscriptionId)
                .stream()
                .map(SubscriptionPaymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPaymentDto> getPaymentHistoryByShop(Long shopId) {
        if (!SecurityContextUtil.isAdmin()) {
            if (!SecurityContextUtil.isShopOwnerOf(shopId)) {
                throw SubscriptionExceptions.subscriptionNotFound();
            }
        }

        return paymentRepository
                .findBySubscription_ShopIdOrderByPaymentDateDesc(shopId)
                .stream()
                .map(SubscriptionPaymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPaymentDto> getAllPaymentHistory() {
        return paymentRepository
                .findAllByOrderByPaymentDateDesc()
                .stream()
                .map(SubscriptionPaymentMapper::toDto)
                .toList();
    }

    //  STRIPE WEBHOOK HANDLERS

    @Override
    @Transactional
    public void handlePaymentSuccess(String stripeSubscriptionId, String transactionId,
                                     String invoiceUrl, BigDecimal amount, String currency) {
        ShopSubscription subscription = subscriptionRepository
                .findByStripeSubscriptionId(stripeSubscriptionId)
                .orElse(null);

        if (subscription == null) {
            log.warn("Webhook payment_succeeded: no subscription for stripeSubId={}", stripeSubscriptionId);
            return;
        }

        // Idempotency guard
        if (transactionId != null && paymentRepository.existsByTransactionId(transactionId)) {
            log.info("Webhook: duplicate payment event transactionId={}, skipping", transactionId);
            return;
        }

        LocalDate today = LocalDate.now();
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(today);
        subscription.setEndDate(today.plusMonths(subscription.getPlan().getDurationMonths()));
        subscription.setPaymentFailureCount(0);
        subscription.setSuspendedAt(null);
        subscriptionRepository.save(subscription);

        SubscriptionPayment payment = SubscriptionPayment.builder()
                .subscription(subscription)
                .amount(amount)
                .currency(currency != null ? currency : subscription.getPlan().getCurrency())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(SubscriptionPaymentMethod.STRIPE)
                .paymentStatus(SubscriptionPaymentStatus.SUCCESS)
                .transactionId(transactionId)
                .invoiceUrl(invoiceUrl)
                .build();
        paymentRepository.save(payment);

        reactivateShopSafely(subscription.getShopId());

        notifyShopOwner(subscription.getShopId(),
                "✅ Your subscription is now ACTIVE until " + subscription.getEndDate() + ".");

        log.info("Subscription ACTIVATED for shopId={}, until={}", subscription.getShopId(), subscription.getEndDate());
    }

    @Override
    @Transactional
    public void handlePaymentFailed(String stripeSubscriptionId, String failureReason) {
        ShopSubscription subscription = subscriptionRepository
                .findByStripeSubscriptionId(stripeSubscriptionId)
                .orElse(null);

        if (subscription == null) {
            log.warn("Webhook payment_failed: no subscription for stripeSubId={}", stripeSubscriptionId);
            return;
        }

        int failures = subscription.getPaymentFailureCount() + 1;
        subscription.setPaymentFailureCount(failures);

        if (Boolean.TRUE.equals(subscription.getAutoRenew())
                && subscription.getStatus() == SubscriptionStatus.ACTIVE) {

            subscription.setStatus(SubscriptionStatus.SUSPENDED);
            subscription.setSuspendedAt(LocalDateTime.now());

            notifyShopOwner(subscription.getShopId(),
                    "⚠️ Auto-renewal payment failed (attempt #" + failures + "). " +
                            "You have 3 days to update your payment method. " +
                            "Reason: " + (failureReason != null ? failureReason : "Card declined"));

            log.warn("Subscription SUSPENDED for shopId={} after autoRenew failure #{}",
                    subscription.getShopId(), failures);

        } else {
            notifyShopOwner(subscription.getShopId(),
                    "❌ " + (failureReason != null ? failureReason : "Payment failed. Please try again."));

            log.warn("Manual payment failed for shopId={}, reason={}", subscription.getShopId(), failureReason);
        }

        subscriptionRepository.save(subscription);

        SubscriptionPayment payment = SubscriptionPayment.builder()
                .subscription(subscription)
                .amount(subscription.getPricePaid() != null ? subscription.getPricePaid() : BigDecimal.ZERO)
                .currency(subscription.getPlan() != null ? subscription.getPlan().getCurrency() : "USD")
                .paymentDate(LocalDateTime.now())
                .paymentMethod(SubscriptionPaymentMethod.STRIPE)
                .paymentStatus(SubscriptionPaymentStatus.FAILED)
                .failureReason(failureReason)
                .build();
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void handleSubscriptionCancelled(String stripeSubscriptionId) {
        subscriptionRepository.findByStripeSubscriptionId(stripeSubscriptionId)
                .ifPresent(sub -> {
                    sub.setStatus(SubscriptionStatus.CANCELLED);
                    sub.setCancelledAt(LocalDateTime.now());
                    subscriptionRepository.save(sub);

                    deactivateShopSafely(sub.getShopId());

                    notifyShopOwner(sub.getShopId(),
                            "Your subscription has been cancelled. Contact support if this was unexpected.");

                    log.info("Subscription CANCELLED via webhook for shopId={}", sub.getShopId());
                });
    }

    //  SCHEDULER METHODS

    @Override
    @Transactional
    public void expireTrials() {
        List<ShopSubscription> expired = subscriptionRepository
                .findByStatusAndTrialEndDateBefore(SubscriptionStatus.TRIAL, LocalDate.now());
        if (expired.isEmpty()) return;

        expired.forEach(sub -> {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            deactivateShopSafely(sub.getShopId());
            notifyShopOwner(sub.getShopId(),
                    "⏰ Your free trial has ended. You now have read-only access. " +
                            "Subscribe to create ads and manage your shop.");
            log.info("Trial EXPIRED for shopId={}", sub.getShopId());
        });

        subscriptionRepository.saveAll(expired);
        log.info("Expired {} trials", expired.size());
    }

    @Override
    @Transactional
    public void expireSubscriptions() {
        List<ShopSubscription> expired = subscriptionRepository
                .findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());
        if (expired.isEmpty()) return;

        expired.forEach(sub -> {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            deactivateShopSafely(sub.getShopId());
            notifyShopOwner(sub.getShopId(),
                    "Your subscription has expired. You now have read-only access. Please renew.");
            log.info("Subscription EXPIRED for shopId={}", sub.getShopId());
        });

        subscriptionRepository.saveAll(expired);
        log.info("Expired {} subscriptions", expired.size());
    }

    @Override
    @Transactional(readOnly = true)
    public void suspendGracePeriodExpired() {
        LocalDateTime graceCutoff = LocalDateTime.now().minusDays(3);
        List<ShopSubscription> overdue = subscriptionRepository
                .findByStatusAndSuspendedAtBefore(SubscriptionStatus.SUSPENDED, graceCutoff);

        overdue.forEach(sub -> {
            notifyShopOwner(sub.getShopId(),
                    "🚫 Your 3-day grace period has ended. " +
                            "You now have read-only access. Please subscribe to restore full access.");
            log.warn("Grace period ENDED for shopId={} — write access now blocked", sub.getShopId());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void sendTrialEndingSoonNotifications() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        List<ShopSubscription> endingSoon = subscriptionRepository
                .findByStatusAndTrialEndDate(SubscriptionStatus.TRIAL, sevenDaysFromNow);

        endingSoon.forEach(sub -> {
            notifyShopOwner(sub.getShopId(),
                    "⏰ Your free trial ends on " + sub.getTrialEndDate() +
                            " (7 days remaining). Subscribe now to keep full access!");
            log.info("Trial ending soon notification sent for shopId={}", sub.getShopId());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void sendSubscriptionExpiringSoonNotifications() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        List<ShopSubscription> expiringSoon = subscriptionRepository
                .findByStatusAndEndDate(SubscriptionStatus.ACTIVE, sevenDaysFromNow);

        expiringSoon.forEach(sub -> {
            String renewMsg = Boolean.TRUE.equals(sub.getAutoRenew())
                    ? "Auto-renew is ON — your card will be charged automatically."
                    : "Please renew manually to avoid losing write access.";

            notifyShopOwner(sub.getShopId(),
                    "⏰ Your subscription expires on " + sub.getEndDate() + " (7 days). " + renewMsg);
            log.info("Subscription expiring soon notification sent for shopId={}", sub.getShopId());
        });
    }

    //  PRIVATE HELPERS

    private String createStripeCustomerForShop(Long shopId) {
        try {
            ShopInfoDto shop = shopService.getShopById(shopId);
            if (shop == null) throw SubscriptionExceptions.shopNotFound();

            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(shop.getOwnerEmail() != null ? shop.getOwnerEmail() : "")
                    .setName(shop.getName())
                    .build();
            Customer customer = Customer.create(params);
            log.info("Stripe customer created lazily for shopId={}, customerId={}", shopId, customer.getId());
            return customer.getId();
        } catch (StripeException e) {
            log.error("Stripe error creating customer for shopId={}: {}", shopId, e.getMessage());
            throw SubscriptionExceptions.stripeError();
        }

    }

    private void deactivateShopSafely(Long shopId) {
        shopService.deactivate(shopId);
    }

    private void reactivateShopSafely(Long shopId) {
        shopService.activate(shopId);
        log.info("Shop reactivated in accounts service. shopId={}", shopId);
    }

    private void notifyShopOwner(Long shopId, String message) {
        ShopInfoDto shop = shopService.getShopById(shopId);
        if (shop != null && shop.getOwnerPhone() != null) {
            log.info("NOTIFY [shopId={}, phone={}]: {}", shopId, shop.getOwnerPhone(), message);
            // TODO: whatsAppService.sendMessage(shop.getOwnerPhone(), message);
        } else {
            log.info("NOTIFY [shopId={}]: {}", shopId, message);
        }
    }
}