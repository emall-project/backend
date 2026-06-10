package store.emall.backend.campaigns.ad.request;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import store.emall.backend.campaigns.ad.payment.AdPaymentDto;
import store.emall.backend.common.page.PaginatedResponse;

import java.util.List;

public interface AdRequestService {

    PaginatedResponse<AdRequestDto> getAll(Pageable pageable, Specification<AdRequest> spec);

    List<AdRequestDto> getAllRequests(Specification<AdRequest> spec);

    AdRequestDto getById(Long id);

    List<AdRequestDto> getByShopId(Long shopId);

    List<AdRequestDto> getByStatus(AdRequestStatus status);

    List<AdRequestDto> getByShopIdAndStatus(Long shopId, AdRequestStatus status);

    List<AdRequestDto> getByTemplateId(Long templateId);

    List<AdRequestDto> getCurrentlyDisplayedAds();

    Long countByStatus(AdRequestStatus status);

    Long countByShopId(Long shopId);

    Long countByTemplateId(Long templateId);

    AdRequestDto createRequest(AdRequestDto dto);

    AdRequestDto updateRequest(AdRequestDto dto);

    void cancelRequest(Long requestId, Long shopId);

    void approveRequest(Long requestId);

    void rejectRequest(Long requestId, String reason);

    void confirmPayment(Long requestId);

    void activatePaidAds();

    void deactivateExpiredAds();

    void sendPaymentReminders();

    void handleOverduePayments();

    AdPaymentResponseDto initiatePayment(Long requestId);

    void handleAdPaymentSuccess(String paymentIntentId);

    List<AdPaymentDto> getPaymentHistoryByShop(Long shopId);

    List<AdPaymentDto> getAllPaymentHistory();

    List<AdPaymentDto> getPaymentHistory(Long adRequestId);

    List<AdPaymentDto> getPaymentHistoryForShop(Long adRequestId);
}
