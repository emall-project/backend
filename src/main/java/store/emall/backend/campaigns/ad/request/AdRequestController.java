package store.emall.backend.campaigns.ad.request;


import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.emall.backend.campaigns.ad.payment.AdPaymentDto;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/ad-requests")
@RequiredArgsConstructor
public class AdRequestController {

    private final AdRequestService adRequestService;

    @GetMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<PaginatedResponse<AdRequestDto>> getAll(
            Pageable pageable, AdRequestSpec spec) {
        PaginatedResponse<AdRequestDto> requests = adRequestService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdRequestDto>> getAllRequests(AdRequestSpec spec) {
        List<AdRequestDto> requests = adRequestService.getAllRequests(spec);
        return EMallsResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@auth.isAdmin() or @auth.isShopOwner()")
    public EMallsResponseEntity<AdRequestDto> getById(@PathVariable @Positive Long id) {
        AdRequestDto request = adRequestService.getById(id);
        return EMallsResponseEntity.ok(request);
    }

    @GetMapping("/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<List<AdRequestDto>> getByShopId(@PathVariable @Positive Long shopId) {
        List<AdRequestDto> requests = adRequestService.getByShopId(shopId);
        return EMallsResponseEntity.ok(requests);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdRequestDto>> getByStatus(@PathVariable AdRequestStatus status) {
        List<AdRequestDto> requests = adRequestService.getByStatus(status);
        return EMallsResponseEntity.ok(requests);
    }

    @GetMapping("/shop/{shopId}/status/{status}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<List<AdRequestDto>> getByShopIdAndStatus(
            @PathVariable @Positive Long shopId, @PathVariable AdRequestStatus status) {
        List<AdRequestDto> requests = adRequestService.getByShopIdAndStatus(shopId, status);
        return EMallsResponseEntity.ok(requests);
    }

    @GetMapping("/template/{templateId}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdRequestDto>> getByTemplateId(
            @PathVariable @Positive Long templateId) {
        List<AdRequestDto> requests = adRequestService.getByTemplateId(templateId);
        return EMallsResponseEntity.ok(requests);
    }

    @GetMapping("/active/displayed")
    public EMallsResponseEntity<List<AdRequestDto>> getCurrentlyDisplayedAds() {
        List<AdRequestDto> ads = adRequestService.getCurrentlyDisplayedAds();
        return EMallsResponseEntity.ok(ads);
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Long> countByStatus(@PathVariable AdRequestStatus status) {
        return EMallsResponseEntity.ok(adRequestService.countByStatus(status));
    }

    @GetMapping("/count/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<Long> countByShopId(@PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(adRequestService.countByShopId(shopId));
    }

    @GetMapping("/count/template/{templateId}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Long> countByTemplateId(@PathVariable @Positive Long templateId) {
        return EMallsResponseEntity.ok(adRequestService.countByTemplateId(templateId));
    }

    @PostMapping
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<AdRequestDto> createRequest(
            @RequestBody @Validated({Default.class, OnCreate.class}) AdRequestDto dto) {
        AdRequestDto created = adRequestService.createRequest(dto);
        return EMallsResponseEntity.created(created);
    }

    @PutMapping
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<AdRequestDto> updateRequest(
            @RequestBody @Validated({Default.class, OnUpdate.class}) AdRequestDto dto) {
        AdRequestDto updated = adRequestService.updateRequest(dto);
        return EMallsResponseEntity.ok(updated);
    }

    @DeleteMapping("/{requestId}/shop/{shopId}")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<Void> cancelRequest(
            @PathVariable @Positive Long requestId,
            @PathVariable @Positive Long shopId) {
        adRequestService.cancelRequest(requestId, shopId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{requestId}/approve")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> approveRequest(@PathVariable @Positive Long requestId) {
        adRequestService.approveRequest(requestId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{requestId}/reject")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> rejectRequest(
            @PathVariable @Positive Long requestId,
            @RequestParam(required = false) String reason) {
        adRequestService.rejectRequest(requestId, reason);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/{requestId}/pay")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> confirmPayment(@PathVariable @Positive Long requestId) {
        adRequestService.confirmPayment(requestId);
        return EMallsResponseEntity.noContent(null);
    }

    @PostMapping("/{requestId}/initiate-payment")
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<AdPaymentResponseDto> initiatePayment(
            @PathVariable @Positive Long requestId) {
        return EMallsResponseEntity.ok(adRequestService.initiatePayment(requestId));
    }

    @GetMapping("/payments")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdPaymentDto>> getAllPaymentHistory() {
        return EMallsResponseEntity.ok(adRequestService.getAllPaymentHistory());
    }

    @GetMapping("/shop/{shopId}/payments")
    @PreAuthorize("@auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<List<AdPaymentDto>> getPaymentHistoryByShop(@PathVariable @Positive Long shopId) {
        return EMallsResponseEntity.ok(adRequestService.getPaymentHistoryByShop(shopId));
    }

    @GetMapping("/{requestId}/payments")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<List<AdPaymentDto>> getPaymentHistory(
            @PathVariable @Positive Long requestId) {
        return EMallsResponseEntity.ok(adRequestService.getPaymentHistory(requestId));
    }

    @GetMapping("/{id}/payments/my")
    @PreAuthorize("@auth.isShopOwner()")
    public EMallsResponseEntity<List<AdPaymentDto>> getPaymentHistoryForShop(@PathVariable @Positive Long id) {
        return EMallsResponseEntity.ok(adRequestService.getPaymentHistoryForShop(id));
    }
}

