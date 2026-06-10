package store.emall.backend.campaigns.offer;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.EMallsResponseEntity;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class OfferExceptions {

    private OfferExceptions() {}

    public static EMallsException offerNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.OFFER_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("offerId", MessageKey.OFFER_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException offerItemNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.OFFER_ITEM_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("offerItemId", MessageKey.OFFER_ITEM_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException offerTitleExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.OFFER_TITLE_EXISTS.getKey())
                .errorCode(List.of(new ErrorCode("title", MessageKey.OFFER_TITLE_EXISTS.getKey())))
                .build();
    }

    public static EMallsException offerStartDateInPast() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.OFFER_START_DATE_IN_PAST.getKey())
                .errorCode(List.of(new ErrorCode("startDate", MessageKey.OFFER_START_DATE_IN_PAST.getKey())))
                .build();
    }

    public static EMallsException offerEndDateNotAfterStart() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.OFFER_END_DATE_NOT_AFTER_START.getKey())
                .errorCode(List.of(new ErrorCode("endDate", MessageKey.OFFER_END_DATE_NOT_AFTER_START.getKey())))
                .build();
    }

    public static EMallsException offerExpired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.OFFER_EXPIRED.getKey())
                .errorCode(List.of(new ErrorCode("offerId", MessageKey.OFFER_EXPIRED.getKey())))
                .build();
    }

    public static EMallsException offerAlreadyExpired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.OFFER_ALREADY_EXPIRED.getKey())
                .errorCode(List.of(new ErrorCode("endDate", MessageKey.OFFER_ALREADY_EXPIRED.getKey())))
                .build();
    }

    public static EMallsException offerNotOwnedByShop() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.OFFER_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("offerId", MessageKey.OFFER_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException productAlreadyInOffer() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.OFFER_PRODUCT_ALREADY_IN_OFFER.getKey())
                .errorCode(List.of(new ErrorCode("productId", MessageKey.OFFER_PRODUCT_ALREADY_IN_OFFER.getKey())))
                .build();
    }

    public static EMallsException productNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.OFFER_PRODUCT_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("productId", MessageKey.OFFER_PRODUCT_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException discountExceedsPrice() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.OFFER_DISCOUNT_EXCEEDS_PRICE.getKey())
                .errorCode(List.of(new ErrorCode("discountValue", MessageKey.OFFER_DISCOUNT_EXCEEDS_PRICE.getKey())))
                .build();
    }

    public static EMallsException invalidPercentValue() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.OFFER_INVALID_PERCENT_VALUE.getKey())
                .errorCode(List.of(new ErrorCode("discountValue", MessageKey.OFFER_INVALID_PERCENT_VALUE.getKey())))
                .build();
    }

    public static EMallsException offerMaxUsesReached() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.OFFER_MAX_USES_REACHED.getKey())
                .errorCode(List.of(new ErrorCode("offerId", MessageKey.OFFER_MAX_USES_REACHED.getKey())))
                .build();
    }

    public static EMallsException shopNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AD_REQUEST_SHOP_NOT_FOUND.getKey())
                .errorCode(List.of(new ErrorCode("shopId", MessageKey.AD_REQUEST_SHOP_NOT_FOUND.getKey())))
                .build();
    }

    public static EMallsException cannotModifyActiveOffer() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.OFFER_CANNOT_MODIFY_ACTIVE.getKey())
                .errorCode(List.of(new ErrorCode("offerId", MessageKey.OFFER_CANNOT_MODIFY_ACTIVE.getKey())))
                .build();
    }

    public static EMallsException noActiveItemsInOffer() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.OFFER_NO_ACTIVE_ITEMS.getKey())
                .errorCode(List.of(new ErrorCode("items", MessageKey.OFFER_NO_ACTIVE_ITEMS.getKey())))
                .build();
    }

    public static EMallsException productAlreadyInOverlappingOffer(String message) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(message)
                .errorCode(List.of(new ErrorCode("items", message)))
                .build();
    }
}