package store.emall.backend.accounts.shop;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class ShopExceptions {

    private ShopExceptions() {}

    public static EMallsException shopNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SHOP_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("shopId", MessageKey.SHOP_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException shopNameExistsInMall() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.SHOP_NAME_EXISTS_IN_MALL.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.SHOP_NAME_EXISTS_IN_MALL.getKey())
                ))
                .build();
    }

    public static EMallsException invalidShopStatus() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.SHOP_INVALID_STATUS.getKey())
                .errorCode(List.of(
                        new ErrorCode("status", MessageKey.SHOP_INVALID_STATUS.getKey())
                ))
                .build();
    }

    public static EMallsException imageNotFound(String fieldName) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.SHOP_IMAGE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode(fieldName, MessageKey.SHOP_IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException invalidFileType(String fieldName) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.SHOP_FILE_INVALID_TYPE.getKey())
                .errorCode(List.of(
                        new ErrorCode(fieldName, MessageKey.SHOP_FILE_INVALID_TYPE.getKey())
                ))
                .build();
    }

    public static EMallsException mediaServiceUnavailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.SHOP_MEDIA_SERVICE_UNAVAILABLE.getKey())
                .errorCode(List.of(
                        new ErrorCode("media", MessageKey.SHOP_MEDIA_SERVICE_UNAVAILABLE.getKey())
                ))
                .build();
    }

    public static EMallsException shopBlocked() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.SHOP_BLOCKED.getKey())
                .errorCode(List.of(
                        new ErrorCode("shopId", MessageKey.SHOP_BLOCKED.getKey())
                ))
                .build();
    }

    public static EMallsException cannotChangeMall() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.SHOP_CANNOT_CHANGE_MALL.getKey())
                .errorCode(List.of(
                        new ErrorCode("mall", MessageKey.SHOP_CANNOT_CHANGE_MALL.getKey())
                ))
                .build();
    }

    public static EMallsException cannotChangeOwner() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.SHOP_CANNOT_CHANGE_OWNER.getKey())
                .errorCode(List.of(
                        new ErrorCode("owner", MessageKey.SHOP_CANNOT_CHANGE_OWNER.getKey())
                ))
                .build();
    }
}