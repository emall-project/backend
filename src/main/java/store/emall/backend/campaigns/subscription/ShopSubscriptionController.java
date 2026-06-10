package store.emall.backend.campaigns.subscription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.campaigns.subscription.payment.SubscriptionPaymentDto;
import store.emall.backend.campaigns.subscription.plan.*;

import java.util.List;

@RestController
// todo remove api prefix
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class ShopSubscriptionController {

    private final ShopSubscriptionService subscriptionService;
    private final SubscriptionPlanService planService;

    //  SUBSCRIPTION PLANS

    @GetMapping("/plans")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<PaginatedResponse<SubscriptionPlanDto>> getAllPlans(
            Pageable pageable, SubscriptionPlanSpec spec) {
        return EMallsResponseEntity.ok(planService.getAll(pageable, spec));
    }

    @GetMapping("/plans/all")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<SubscriptionPlanDto>> getAllPlansList(SubscriptionPlanSpec spec) {
        return EMallsResponseEntity.ok(planService.getAllPlans(spec));
    }

    @GetMapping("/plans/active")
    @PreAuthorize("@auth.isAdmin() or @auth.isShopOwner()")
    public EMallsResponseEntity<List<SubscriptionPlanDto>> getActivePlans() {
        return EMallsResponseEntity.ok(planService.getActivePlans());
    }

    @GetMapping("/plans/{planId}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<SubscriptionPlanDto> getPlanById(
            @PathVariable @Positive Long planId) {
        return EMallsResponseEntity.ok(planService.getById(planId));
    }

    @PostMapping("/plans")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<SubscriptionPlanDto> createPlan(
            @RequestBody @Validated({Default.class, OnCreate.class}) SubscriptionPlanDto dto) {
        return EMallsResponseEntity.created(planService.create(dto));
    }

    @PutMapping("/plans")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<SubscriptionPlanDto> updatePlan(
            @RequestBody @Validated({Default.class, OnUpdate.class}) SubscriptionPlanDto dto) {
        return EMallsResponseEntity.ok(planService.update(dto));
    }

    //  SUBSCRIPTIONS — Admin reads

    @GetMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<PaginatedResponse<ShopSubscriptionDto>> getAll(
            Pageable pageable, ShopSubscriptionSpec spec) {
        return EMallsResponseEntity.ok(subscriptionService.getAll(pageable, spec));
    }

    @GetMapping("/all")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<ShopSubscriptionDto>> getAllSubscriptions(
            ShopSubscriptionSpec spec) {
        return EMallsResponseEntity.ok(subscriptionService.getAllSubscriptions(spec));
    }

    @GetMapping("/{subscriptionId}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<ShopSubscriptionDto> getById(
            @PathVariable @Positive Long subscriptionId) {
        return EMallsResponseEntity.ok(subscriptionService.getById(subscriptionId));
    }

    @PutMapping("/{subscriptionId}/cancel")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> cancel(
            @PathVariable @Positive Long subscriptionId) {
        subscriptionService.cancel(subscriptionId);
        return EMallsResponseEntity.noContent(null);
    }

    // PAYMENT HISTORY

    @GetMapping("/{subscriptionId}/payments")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<SubscriptionPaymentDto>> getPaymentHistory(
            @PathVariable @Positive Long subscriptionId) {
        return EMallsResponseEntity.ok(subscriptionService.getPaymentHistory(subscriptionId));
    }

    @GetMapping("/{subscriptionId}/payments/my")
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<List<SubscriptionPaymentDto>> getPaymentHistoryForShop(
            @PathVariable @Positive Long subscriptionId) {
        return EMallsResponseEntity.ok(subscriptionService.getPaymentHistoryForShop(subscriptionId));
    }

    @GetMapping("/shop/{shopId}/payments")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<List<SubscriptionPaymentDto>> getPaymentHistoryByShop(
            @PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(subscriptionService.getPaymentHistoryByShop(shopId));
    }

    @GetMapping("/payments")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<SubscriptionPaymentDto>> getAllPaymentHistory() {
        return EMallsResponseEntity.ok(subscriptionService.getAllPaymentHistory());
    }

    //  SUBSCRIPTIONS

    @GetMapping("/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<ShopSubscriptionDto> getByShopId(
            @PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(subscriptionService.getByShopId(shopId));
    }

    @GetMapping("/shop/{shopId}/status")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<SubscriptionStatusDto> getSubscriptionStatus(
            @PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(subscriptionService.getSubscriptionStatus(shopId));
    }

    //TODO: remove it, check it from accounts now
    @GetMapping("/shop/{shopId}/write-access")
    public EMallsResponseEntity<Boolean> hasWriteAccess(
            @PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(subscriptionService.hasWriteAccess(shopId));
    }

    //  SUBSCRIBE

    @PostMapping("/subscribe")
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<SubscribeResponseDto> subscribe(
            @RequestBody @Valid SubscribeRequestDto dto) {
        return EMallsResponseEntity.ok(subscriptionService.subscribe(dto));
    }

    //  TRIAL — Internal (accounts service Feign, no JWT)

    @PostMapping("/trial")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public EMallsResponseEntity<ShopSubscriptionDto> createTrial(
            @RequestParam @Positive Long shopId,
            @RequestParam String shopEmail,
            @RequestParam String shopName) {
        return EMallsResponseEntity.created(
                subscriptionService.createTrial(shopId, shopEmail, shopName));
    }
}