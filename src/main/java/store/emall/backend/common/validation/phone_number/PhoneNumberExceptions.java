package store.emall.backend.common.validation.phone_number;

import org.springframework.http.HttpStatus;
import store.emall.backend.accounts.common.exception.EMallsException;
import store.emall.backend.accounts.common.message.MessageKey;
import store.emall.backend.accounts.common.response.ErrorCode;

import java.util.List;

public final class PhoneNumberExceptions {

    private PhoneNumberExceptions() {}

    public static EMallsException phoneRequired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PHONE_NUMBER_REQUIRED.getKey())
                .errorCode(List.of(
                        new ErrorCode("phone.number",
                                MessageKey.PHONE_NUMBER_REQUIRED.getKey())
                ))
                .build();
    }

    public static EMallsException invalidPhoneNumber() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PHONE_NUMBER_INVALID.getKey())
                .errorCode(List.of(
                        new ErrorCode("phone.number",
                                MessageKey.PHONE_NUMBER_INVALID.getKey())
                ))
                .build();
    }

    public static EMallsException invalidPrefix() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.PHONE_PREFIX_INVALID.getKey())
                .errorCode(List.of(
                        new ErrorCode("phone.prefix",
                                MessageKey.PHONE_PREFIX_INVALID.getKey())
                ))
                .build();
    }
}
