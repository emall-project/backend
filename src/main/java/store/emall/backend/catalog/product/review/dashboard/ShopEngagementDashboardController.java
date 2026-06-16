package store.emall.backend.catalog.product.review.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.emall.backend.common.response.EMallsResponseEntity;

@RestController
@RequestMapping("/shops/{shopId}/engagement")
@RequiredArgsConstructor
public class ShopEngagementDashboardController {

    private final ShopEngagementDashboardService dashboardService;

    @GetMapping("/reviews/summary")
    @PreAuthorize("@auth.isAdmin() or @auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<ShopReviewSummaryDto> getReviewSummary(@PathVariable Long shopId) {
        return EMallsResponseEntity.ok(dashboardService.getReviewSummary(shopId));
    }

    @GetMapping("/comments/summary")
    @PreAuthorize("@auth.isAdmin() or @auth.isAdminOrShopOwnerOf(#shopId)")
    public EMallsResponseEntity<ShopCommentSummaryDto> getCommentSummary(@PathVariable Long shopId) {
        return EMallsResponseEntity.ok(dashboardService.getCommentSummary(shopId));
    }
}
