package ps.emall.mediamanager.file;

import org.springframework.http.HttpStatus;
import ps.emall.mediamanager.client.common.dto.Reference;
import ps.emall.mediamanager.common.SystemService;
import ps.emall.mediamanager.common.exception.EMallsException;
import ps.emall.mediamanager.common.message.MessageKey;
import ps.emall.mediamanager.common.response.ErrorCode;

import java.util.ArrayList;
import java.util.List;

public final class FileExceptions {

    private FileExceptions() {
    }

    public static EMallsException fileNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.FILE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("id", MessageKey.FILE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException fileNameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.FILE_NAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.FILE_NAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException storeIdMisMatch() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FILE_STORE_ID_MISMATCH.getKey())
                .errorCode(List.of(
                        new ErrorCode("storeId", MessageKey.FILE_STORE_ID_MISMATCH.getKey())
                ))
                .build();
    }

    public static EMallsException fileScopeMismatch() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FILE_SCOPE_MISMATCH.getKey())
                .errorCode(List.of(
                        new ErrorCode("scope", MessageKey.FILE_SCOPE_MISMATCH.getKey())
                ))
                .build();
    }

    public static EMallsException changingFileScopeNotAllowed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CHANGING_FILE_SCOPE_NOT_ALLOWED.getKey())
                .errorCode(List.of(
                        new ErrorCode("scope", MessageKey.CHANGING_FILE_SCOPE_NOT_ALLOWED.getKey())
                ))
                .build();
    }

    public static EMallsException changingFileManagerNotAllowed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CHANGING_FILE_MANAGER_NOT_ALLOWED.getKey())
                .errorCode(List.of(
                        new ErrorCode("managedBy", MessageKey.CHANGING_FILE_MANAGER_NOT_ALLOWED.getKey())
                ))
                .build();
    }

    public static EMallsException scopeNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FILE_SCOPE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("scope", MessageKey.FILE_SCOPE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException managedByNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FILE_MANAGED_BY_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("managedBy", MessageKey.FILE_MANAGED_BY_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException fileInUse(List<Reference> references) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.FILE_IN_USE.getKey())
                .params(new ArrayList<>(references))
                .build();
    }

    public static EMallsException fileInUseValidationFailed(SystemService systemService) {
        return EMallsException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .message(MessageKey.FILE_IN_USE_VALIDATION_FAILED.getKey())
                .errorCode(List.of(
                        new ErrorCode(
                                systemService.getServiceName(),
                                MessageKey.FILE_IN_USE_VALIDATION_FAILED.getKey()
                        )
                ))
                .build();
    }

}