package store.emall.backend.accounts.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.emall.backend.accounts.request.shopowner.ShopOwnerRequest;
import store.emall.backend.accounts.shop.Shop;
import store.emall.backend.accounts.user.User;

@Service
@Slf4j
public class ShopRequestNotificationService {

    public void notifyAdminNewRequest(ShopOwnerRequest request) {
        log.info("[NOTIFICATION → ADMIN] New shop owner request submitted: " +
                        "requestId={}, username={}, shopName={}",
                request.getId(),
                request.getUsername(),
                request.getShopRequest() != null ? request.getShopRequest().getName() : "N/A");
        // TODO: integrate with notification microservice:
        // notificationClient.notifyAdmin(NotificationDto.forNewShopRequest(request));
    }

    public void notifyOwnerApproved(ShopOwnerRequest request, User createdUser, Shop createdShop) {
        log.info("[NOTIFICATION → SHOP_OWNER] Request APPROVED: " +
                        "requestId={}, username={}, shopId={}, shopName={}",
                request.getId(),
                createdUser.getUsername(),
                createdShop.getShopId(),
                createdShop.getName());
        // TODO: send approval whatsapp/SMS to request.getPhoneNumber()
    }

    public void notifyExistingShopOwnerApproved(User request, Shop createdShop) {
        log.info("[NOTIFICATION → EXIST_SHOP_OWNER] Request APPROVED: " +
                        "requestId={}, shopId={}, shopName={}",
                request.getUserId(),
                createdShop.getShopId(),
                createdShop.getName());
        // TODO: send approval whatsapp/SMS to request.getPhoneNumber()
    }

    public void notifyOwnerRejected(ShopOwnerRequest request, String reason) {
        log.info("[NOTIFICATION → SHOP_OWNER] Request REJECTED: " +
                        "requestId={}, username={}, reason={}",
                request.getId(),
                request.getUsername(),
                reason);
        // TODO: send rejection whatsapp/SMS to request.getPhoneNumber()
    }

    public void notifyExistingShopOwnerRejected(User request, String reason) {
        log.info("[NOTIFICATION → EXIST_SHOP_OWNER] Request REJECTED: " +
                        "requestId={}, username={}, reason={}",
                request.getUserId(),
                request.getUsername(),
                reason);
        // TODO: send rejection whatsapp/SMS to request.getPhoneNumber()
    }
}