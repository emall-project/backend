package store.emall.backend.catalog.product.review.comment;

import jakarta.validation.constraints.*;
import lombok.*;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;
import store.emall.backend.catalog.product.ProductDto;
import store.emall.backend.catalog.product.review.moderation.CommentModerationLogDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCommentDto {

    private Long commentId;
    private Long productId;

    @NotNull(groups = OnCreate.class, message = "comment.userId.notnull")
    @Positive(message = "comment.userId.positive")
    private Long userId;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "comment.content.notblank")
    @Size(min = 2, max = 2000, message = "comment.content.size")
    private String content;

    private CommentStatus status;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private ProductDto product;

    private String productName;

    private String productUrl;

    private List<CommentModerationLogDto> moderationLogs;

    private Integer moderationRetryCount;

}