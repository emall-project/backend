package store.emall.backend.accounts.mall;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class MallExceptions {

    private MallExceptions() {}

    public static EMallsException mallNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.MALL_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("mallId", MessageKey.MALL_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException mallNameExistsInCity() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.MALL_NAME_EXISTS_IN_CITY.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.MALL_NAME_EXISTS_IN_CITY.getKey())
                ))
                .build();
    }

    public static EMallsException invalidMallStatus() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.MALL_INVALID_STATUS.getKey())
                .errorCode(List.of(
                        new ErrorCode("status", MessageKey.MALL_INVALID_STATUS.getKey())
                ))
                .build();
    }

    public static EMallsException imageNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.MALL_IMAGE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("image", MessageKey.MALL_IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException invalidFileType() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.MALL_FILE_INVALID_TYPE.getKey())
                .errorCode(List.of(
                        new ErrorCode("file", MessageKey.MALL_IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException mediaServiceUnavailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.MALL_MEDIA_SERVICE_UNAVAILABLE.getKey())
                .errorCode(List.of(
                        new ErrorCode("media", MessageKey.MALL_IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException mallHasActiveShops() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.MALL_HAS_ACTIVE_SHOPS.getKey())
                .errorCode(List.of(
                        new ErrorCode("mallId", MessageKey.MALL_HAS_ACTIVE_SHOPS.getKey())
                ))
                .build();
    }

}
