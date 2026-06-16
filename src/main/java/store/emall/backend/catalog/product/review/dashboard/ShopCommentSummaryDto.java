package store.emall.backend.catalog.product.review.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.emall.backend.catalog.product.review.comment.ProductCommentDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopCommentSummaryDto {
    private Long shopId;
    private long totalComments;
    private long approvedComments;
    private long pendingComments;
    private long reportedComments;
    private long flaggedComments;
    private long rejectedComments;
    private List<ProductCommentDto> recentComments;
}
