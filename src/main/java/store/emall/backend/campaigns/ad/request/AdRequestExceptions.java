package store.emall.backend.campaigns.ad.request;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class AdRequestExceptions {

    private AdRequestExceptions() {}

    public static EMallsException requestNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AD_REQUEST_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("requestId", MessageKey.AD_REQUEST_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException templateNotActive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_TEMPLATE_NOT_ACTIVE.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_REQUEST_TEMPLATE_NOT_ACTIVE.getKey())
                ))
                .build();
    }

    public static EMallsException duplicateRequestForShop() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_REQUEST_DUPLICATE_FOR_SHOP.getKey())
                .errorCode(List.of(
                        new ErrorCode("shopId", MessageKey.AD_REQUEST_DUPLICATE_FOR_SHOP.getKey())
                ))
                .build();
    }

    public static EMallsException requestAlreadyProcessed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_ALREADY_PROCESSED.getKey())
                .errorCode(List.of(
                        new ErrorCode("requestId", MessageKey.AD_REQUEST_ALREADY_PROCESSED.getKey())
                ))
                .build();
    }

    public static EMallsException requestNotApproved() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_NOT_APPROVED.getKey())
                .errorCode(List.of(
                        new ErrorCode("requestId", MessageKey.AD_REQUEST_NOT_APPROVED.getKey())
                ))
                .build();
    }

    public static EMallsException paymentAlreadyMade() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_REQUEST_PAYMENT_ALREADY_MADE.getKey())
                .errorCode(List.of(
                        new ErrorCode("requestId", MessageKey.AD_REQUEST_PAYMENT_ALREADY_MADE.getKey())
                ))
                .build();
    }

    public static EMallsException cannotModifyProcessedRequest() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_CANNOT_MODIFY_PROCESSED.getKey())
                .errorCode(List.of(
                        new ErrorCode("requestId", MessageKey.AD_REQUEST_CANNOT_MODIFY_PROCESSED.getKey())
                ))
                .build();
    }

    public static EMallsException templateAlreadyReserved() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_REQUEST_TEMPLATE_ALREADY_RESERVED.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_REQUEST_TEMPLATE_ALREADY_RESERVED.getKey())
                ))
                .build();
    }

    public static EMallsException templateTimeSlotTaken() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_REQUEST_TIME_SLOT_TAKEN.getKey())
                .errorCode(List.of(
                        new ErrorCode("startDate", MessageKey.AD_REQUEST_TIME_SLOT_TAKEN.getKey()),
                        new ErrorCode("endDate",   MessageKey.AD_REQUEST_TIME_SLOT_TAKEN.getKey())
                ))
                .build();
    }

    public static EMallsException paymentNotMade() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_PAYMENT_NOT_MADE.getKey())
                .errorCode(List.of(
                        new ErrorCode("requestId", MessageKey.AD_REQUEST_TEMPLATE_ALREADY_RESERVED.getKey())
                ))
                .build();
    }

    public static EMallsException shopNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AD_REQUEST_SHOP_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("shopId", MessageKey.AD_REQUEST_SHOP_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException templateDateExpired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_TEMPLATE_DATE_EXPIRED.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_REQUEST_TEMPLATE_DATE_EXPIRED.getKey())
                ))
                .build();
    }

    public static EMallsException templateStartDatePassed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_TEMPLATE_START_DATE_PASSED.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_REQUEST_TEMPLATE_START_DATE_PASSED.getKey())
                ))
                .build();
    }

    public static EMallsException paymentOverdue() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_REQUEST_PAYMENT_OVERDUE.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_REQUEST_PAYMENT_OVERDUE.getKey())
                ))
                .build();
    }

    public static EMallsException requestNotFoundForShop() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AD_REQUEST_NOT_FOUND_FOR_SHOP.getKey())
                .errorCode(List.of(
                        new ErrorCode("shopId", MessageKey.AD_REQUEST_NOT_FOUND_FOR_SHOP.getKey())
                ))
                .build();
    }

    public static EMallsException imageNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.IMAGE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("adRequestImageUuid", MessageKey.IMAGE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException invalidFileType() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FILE_INVALID_TYPE.getKey())
                .errorCode(List.of(
                        new ErrorCode("adRequestImageUuid", MessageKey.FILE_INVALID_TYPE.getKey())
                ))
                .build();
    }

    public static EMallsException mediaServiceUnavailable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.MEDIA_SERVICE_UNAVAILABLE.getKey())
                .errorCode(List.of(
                        new ErrorCode("media", MessageKey.MEDIA_SERVICE_UNAVAILABLE.getKey())
                ))
                .build();
    }

}
