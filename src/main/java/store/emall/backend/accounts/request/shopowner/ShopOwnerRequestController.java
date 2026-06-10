package store.emall.backend.accounts.request.shopowner;

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
import store.emall.backend.accounts.request.admin.AdminDecisionDto;
import store.emall.backend.accounts.request.shop.ExistingOwnerShopRequestDto;
import store.emall.backend.accounts.request.shop.ExistingOwnerShopRequestSpec;
import store.emall.backend.accounts.request.shop.ShopRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/shop-owner-requests")
@RequiredArgsConstructor
public class ShopOwnerRequestController {

    private final ShopOwnerRequestService shopOwnerRequestService;

    @PostMapping
    public EMallsResponseEntity<ShopOwnerRequestDto> submitRequest(
            @RequestBody @Validated({Default.class, OnCreate.class}) ShopOwnerRequestDto dto) {
        ShopOwnerRequestDto result = shopOwnerRequestService.submitRequest(dto);
        return EMallsResponseEntity.created(result);
    }


    @PostMapping("/existing-owner/shop-request")
    @PreAuthorize("hasAuthority('ROLE_SHOP_OWNER')")
    public EMallsResponseEntity<ShopRequestDto> submitShopRequestForExistingOwner(
            @RequestBody @Validated({Default.class, OnCreate.class}) ExistingOwnerShopRequestDto dto) {
        ShopRequestDto result = shopOwnerRequestService.submitShopRequestForExistingOwner(dto);
        return EMallsResponseEntity.created(result);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<PaginatedResponse<ShopOwnerRequestDto>> getAll(
            Pageable pageable,
            ShopOwnerRequestSpec spec) {
        PaginatedResponse<ShopOwnerRequestDto> result = shopOwnerRequestService.getAll(pageable, spec);
        return EMallsResponseEntity.ok(result);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<List<ShopOwnerRequestDto>> getAllPending() {
        List<ShopOwnerRequestDto> result = shopOwnerRequestService.getAllPending();
        return EMallsResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<ShopOwnerRequestDto> getById(@PathVariable @Positive Long id) {
        ShopOwnerRequestDto result = shopOwnerRequestService.getById(id);
        return EMallsResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<List<ShopOwnerRequestDto>> getAllList(
            ShopOwnerRequestSpec spec) {
        List<ShopOwnerRequestDto> result = shopOwnerRequestService.getAllList(spec);
        return EMallsResponseEntity.ok(result);
    }

    @GetMapping("/existing-owner/shop-requests")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<PaginatedResponse<ShopRequestDto>> getAllExistingOwnerRequests(
            Pageable pageable,
            ExistingOwnerShopRequestSpec spec) {
        PaginatedResponse<ShopRequestDto> result =
                shopOwnerRequestService.getAllExistingOwnerRequests(pageable, spec);
        return EMallsResponseEntity.ok(result);
    }

    @GetMapping("/existing-owner/shop-requests/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<List<ShopRequestDto>> getAllExistingOwnerRequestsList(
            ExistingOwnerShopRequestSpec spec) {
        List<ShopRequestDto> result =
                shopOwnerRequestService.getAllExistingOwnerRequestsList(spec);
        return EMallsResponseEntity.ok(result);
    }

    @GetMapping("/existing-owner/shop-requests/{existingOwnerShopRequestId}")
    public EMallsResponseEntity<ShopRequestDto> getExistingOwnerRequestById(@PathVariable @Positive Long existingOwnerShopRequestId) {
        ShopRequestDto result = shopOwnerRequestService.getExistingOwnerRequestById(existingOwnerShopRequestId);
        return EMallsResponseEntity.ok(result);
    }

    @PutMapping("/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> approve(@RequestBody @Valid AdminDecisionDto decision) {
        shopOwnerRequestService.approve(decision);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> reject(@RequestBody @Valid AdminDecisionDto decision) {
        shopOwnerRequestService.reject(decision);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/shop-requests/{shopRequestId}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> approveShopRequest(@PathVariable @Positive Long shopRequestId) {
        shopOwnerRequestService.approveShopRequest(shopRequestId);
        return EMallsResponseEntity.noContent(null);
    }

    @PutMapping("/shop-requests/{shopRequestId}/reject")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public EMallsResponseEntity<Void> rejectShopRequest(
            @PathVariable @Positive Long shopRequestId,
            @RequestParam String rejectionReason) {
        shopOwnerRequestService.rejectShopRequest(shopRequestId, rejectionReason);
        return EMallsResponseEntity.noContent(null);
    }
}