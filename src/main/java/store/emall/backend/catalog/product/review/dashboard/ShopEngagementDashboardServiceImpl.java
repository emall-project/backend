package store.emall.backend.catalog.product.review.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.catalog.product.review.comment.CommentStatus;
import store.emall.backend.catalog.product.review.comment.ProductCommentMapper;
import store.emall.backend.catalog.product.review.comment.ProductCommentRepository;
import store.emall.backend.catalog.product.review.rating.ProductReviewMapper;
import store.emall.backend.catalog.product.review.rating.ProductReviewRepository;

@Service
@RequiredArgsConstructor
public class ShopEngagementDashboardServiceImpl implements ShopEngagementDashboardService {

    private final ProductReviewRepository reviewRepository;
    private final ProductCommentRepository commentRepository;

    @Override
    public ShopReviewSummaryDto getReviewSummary(Long shopId) {
        double averageRating = java.util.Optional
                .ofNullable(reviewRepository.findAverageRatingByShopId(shopId))
                .orElse(0.0);

        return ShopReviewSummaryDto.builder()
                .shopId(shopId)
                .totalReviews(reviewRepository.countByProduct_ShopId(shopId))
                .averageRating(averageRating)
                .reviewedProducts(reviewRepository.countReviewedProductsByShopId(shopId))
                .recentReviews(
                        reviewRepository.findTop5ByProduct_ShopIdOrderByCreatedAtDesc(shopId)
                                .stream()
                                .map(ProductReviewMapper::toDto)
                                .toList()
                )
                .build();
    }

    @Override
    public ShopCommentSummaryDto getCommentSummary(Long shopId) {
        return ShopCommentSummaryDto.builder()
                .shopId(shopId)
                .totalComments(commentRepository.countByProduct_ShopId(shopId))
                .approvedComments(commentRepository.countByProduct_ShopIdAndStatus(shopId, CommentStatus.APPROVED))
                .pendingComments(commentRepository.countByProduct_ShopIdAndStatus(shopId, CommentStatus.PENDING_MODERATION))
                .reportedComments(commentRepository.countByProduct_ShopIdAndStatus(shopId, CommentStatus.REPORTED))
                .flaggedComments(commentRepository.countByProduct_ShopIdAndStatus(shopId, CommentStatus.FLAGGED))
                .rejectedComments(commentRepository.countByProduct_ShopIdAndStatus(shopId, CommentStatus.REJECTED))
                .recentComments(
                        commentRepository.findTop5ByProduct_ShopIdOrderByCreatedAtDesc(shopId)
                                .stream()
                                .map(ProductCommentMapper::toDtoWithProductInfo)
                                .toList()
                )
                .build();
    }
}
