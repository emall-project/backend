package store.emall.backend.accounts.request.shopowner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.common.page.PaginatedResponse;
import store.emall.backend.accounts.request.admin.AdminDecisionDto;
import store.emall.backend.accounts.request.shop.ExistingOwnerShopRequestDto;
import store.emall.backend.accounts.request.shop.ShopRequest;
import store.emall.backend.accounts.request.shop.ShopRequestDto;

import java.util.List;

public interface ShopOwnerRequestService {

    ShopOwnerRequestDto submitRequest(ShopOwnerRequestDto dto);
    ShopRequestDto submitShopRequestForExistingOwner(ExistingOwnerShopRequestDto dto);

    // Admin views
    PaginatedResponse<ShopOwnerRequestDto> getAll(Pageable pageable, Specification<ShopOwnerRequest> spec);
    List<ShopOwnerRequestDto> getAllPending();
    ShopOwnerRequestDto getById(Long id);
    ShopOwnerRequestDto getByIdForOwner(Long id);

    List<ShopOwnerRequestDto> getAllList(Specification<ShopOwnerRequest> spec);
    PaginatedResponse<ShopRequestDto> getAllExistingOwnerRequests(Pageable pageable, Specification<ShopRequest> spec);
    List<ShopRequestDto> getAllExistingOwnerRequestsList(Specification<ShopRequest> spec);
    ShopRequestDto getExistingOwnerRequestById(Long existingOwnerShopRequestId);

    void approve(AdminDecisionDto decision);
    void reject(AdminDecisionDto decision);

    void approveShopRequest(Long shopRequestId);
    void rejectShopRequest(Long shopRequestId, String rejectionReason);
}