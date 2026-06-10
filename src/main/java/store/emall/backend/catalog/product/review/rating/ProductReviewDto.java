package store.emall.backend.catalog.product.review.rating;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewDto {

    @Null(groups = OnCreate.class, message = "review.reviewId.null")
    @NotNull(groups = OnUpdate.class, message = "review.reviewId.notnull")
    @Positive(message = "review.reviewId.positive")
    private Long reviewId;

    private Long productId;

    @NotNull(groups = OnCreate.class, message = "review.userId.notnull")
    @Positive(message = "review.userId.positive")
    private Long userId;

    @NotNull(groups = OnCreate.class, message = "review.rating.notnull")
    @Min(value = 1, message = "review.rating.min")
    @Max(value = 5, message = "review.rating.max")
    private Short rating;

    private Double averageRating;
    private Long totalReviews;
}