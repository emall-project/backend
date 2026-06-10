package store.emall.backend.campaigns.ad.template;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class AdTemplateExceptions {

    private AdTemplateExceptions() {}

    public static EMallsException templateNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.AD_TEMPLATE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException templateNameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_TEMPLATE_NAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.AD_TEMPLATE_NAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException startDateInPast() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_TEMPLATE_START_DATE_IN_PAST.getKey())
                .errorCode(List.of(new ErrorCode("startDate", MessageKey.AD_TEMPLATE_START_DATE_IN_PAST.getKey())))
                .build();
    }

    public static EMallsException endDateNotAfterStartDate() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_TEMPLATE_END_DATE_NOT_AFTER_START.getKey())
                .errorCode(List.of(new ErrorCode("endDate", MessageKey.AD_TEMPLATE_END_DATE_NOT_AFTER_START.getKey())))
                .build();
    }

    public static EMallsException templateNotActive() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_TEMPLATE_NOT_ACTIVE.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_NOT_ACTIVE.getKey())
                ))
                .build();
    }

    public static EMallsException templateAlreadyReserved() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_TEMPLATE_ALREADY_RESERVED.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_ALREADY_RESERVED.getKey())
                ))
                .build();
    }

    public static EMallsException templateHasActiveRequests() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_TEMPLATE_HAS_ACTIVE_REQUESTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_HAS_ACTIVE_REQUESTS.getKey())
                ))
                .build();
    }

    public static EMallsException templateDateExpired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.AD_TEMPLATE_DATE_EXPIRED.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_DATE_EXPIRED.getKey())
                ))
                .build();
    }

    public static EMallsException positionDateRangeConflict() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_TEMPLATE_POSITION_DATE_CONFLICT.getKey())
                .errorCode(List.of(
                        new ErrorCode("position", MessageKey.AD_TEMPLATE_POSITION_DATE_CONFLICT.getKey())
                ))
                .build();
    }

    public static EMallsException templateArchived() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_TEMPLATE_ARCHIVED.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_ARCHIVED.getKey())
                ))
                .build();
    }

    public static EMallsException templateReservedImmutable() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.AD_TEMPLATE_RESERVED_IMMUTABLE.getKey())
                .errorCode(List.of(
                        new ErrorCode("templateId", MessageKey.AD_TEMPLATE_RESERVED_IMMUTABLE.getKey())
                ))
                .build();
    }

}
