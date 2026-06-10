package store.emall.backend.catalog.product.review.moderation;

import store.emall.backend.catalog.product.review.comment.ProductComment;

public interface ModerationService {

    void enqueue(ProductComment comment);

    void moderateSync(ProductComment comment);
}