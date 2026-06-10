package store.emall.backend.accounts.mall.service;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class MallServiceExceptions {

    private MallServiceExceptions() {}

    public static EMallsException serviceNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.MALL_SERVICE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("serviceId", MessageKey.MALL_SERVICE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException serviceNameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.MALL_SERVICE_NAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.MALL_SERVICE_NAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException servicesBelongToDifferentMalls() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.MALL_SERVICE_BATCH_DIFFERENT_MALLS.getKey())
                .errorCode(List.of(
                        new ErrorCode("batch", MessageKey.MALL_SERVICE_BATCH_DIFFERENT_MALLS.getKey())
                ))
                .build();
    }
}
