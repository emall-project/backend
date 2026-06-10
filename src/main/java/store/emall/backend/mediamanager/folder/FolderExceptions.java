package store.emall.backend.mediamanager.folder;

import org.springframework.http.HttpStatus;
import store.emall.backend.common.exception.EMallsException;
import store.emall.backend.common.message.MessageKey;
import store.emall.backend.common.response.ErrorCode;

import java.util.List;

public final class FolderExceptions {

    private FolderExceptions() {
    }

    public static EMallsException folderNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(MessageKey.FOLDER_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("id", MessageKey.FOLDER_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException folderNameExists() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message(MessageKey.FOLDER_NAME_EXISTS.getKey())
                .errorCode(List.of(
                        new ErrorCode("name", MessageKey.FOLDER_NAME_EXISTS.getKey())
                ))
                .build();
    }

    public static EMallsException folderHierarchyCyclic() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FOLDER_HIERARCHY_CYCLIC.getKey())
                .errorCode(List.of(
                        new ErrorCode("parentId", MessageKey.FOLDER_HIERARCHY_CYCLIC.getKey())
                ))
                .build();
    }

    public static EMallsException storeIdMismatch() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FOLDER_STORE_ID_MISMATCH.getKey())
                .errorCode(List.of(
                        new ErrorCode("storeId", MessageKey.FOLDER_STORE_ID_MISMATCH.getKey())
                ))
                .build();
    }

    public static EMallsException changingFolderScopeNotAllowed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CHANGING_FOLDER_SCOPE_NOT_ALLOWED.getKey())
                .errorCode(List.of(
                        new ErrorCode("storeId", MessageKey.CHANGING_FOLDER_SCOPE_NOT_ALLOWED.getKey())
                ))
                .build();
    }

    public static EMallsException rootFolderCreationNotAllowed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message(MessageKey.ROOT_FOLDER_CREATION_NOT_ALLOWED.getKey())
                .errorCode(List.of(
                        new ErrorCode("parentId", MessageKey.ROOT_FOLDER_CREATION_NOT_ALLOWED.getKey())
                ))
                .build();
    }

    public static EMallsException folderScopeMismatch() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FOLDER_SCOPE_MISMATCH.getKey())
                .errorCode(List.of(
                        new ErrorCode("scope", MessageKey.FOLDER_SCOPE_MISMATCH.getKey())
                ))
                .build();
    }

    public static EMallsException changingFolderManagerNotAllowed() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.CHANGING_FOLDER_MANAGER_NOT_ALLOWED.getKey())
                .errorCode(List.of(
                        new ErrorCode("managedBy", MessageKey.CHANGING_FOLDER_MANAGER_NOT_ALLOWED.getKey())
                ))
                .build();
    }

    public static EMallsException scopeNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FOLDER_SCOPE_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("scope", MessageKey.FOLDER_SCOPE_NOT_FOUND.getKey())
                ))
                .build();
    }

    public static EMallsException managedByNotFound() {
        return EMallsException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(MessageKey.FOLDER_MANAGED_BY_NOT_FOUND.getKey())
                .errorCode(List.of(
                        new ErrorCode("managedBy", MessageKey.FOLDER_MANAGED_BY_NOT_FOUND.getKey())
                ))
                .build();
    }
}