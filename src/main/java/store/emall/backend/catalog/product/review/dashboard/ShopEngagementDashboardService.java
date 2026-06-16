package store.emall.backend.catalog.product.review.dashboard;

public interface ShopEngagementDashboardService {
    ShopReviewSummaryDto getReviewSummary(Long shopId);
    ShopCommentSummaryDto getCommentSummary(Long shopId);
}
