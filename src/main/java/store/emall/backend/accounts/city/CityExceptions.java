package store.emall.backend.accounts.city;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class CityExceptions {

    private CityExceptions() {}

    public static EMallsException cityNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.CITY_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("cityId", MessageKey.CITY_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException cityNameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.CITY_NAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.CITY_NAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException cityIdRequired() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CITY_ID_REQUIRED.getKey())
                .errorCode(List.of(
                        new ErrorCode("city.cityId", MessageKey.CITY_ID_REQUIRED.getKey())
                ))
                .build();
    }
}
