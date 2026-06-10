package store.emall.backend.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageKey {

    // ==================== File VALIDATION ====================
    FILE_FILE_NOT_NULL("file.file.notNull"),
    FILE_ID_NOT_NULL("file.id.notNull"),
    FILE_NAME_NOT_NULL("file.name.notNull"),
    FILE_EXTENSION_NOT_NULL("file.extension.notNull"),
    FILE_FOLDERID_NOT_NULL("file.folderId.notNull"),
    FILE_FOLDERID_POSITIVE("file.folderId.positive"),

    // ==================== File Error ====================
    FILE_NOT_FOUND("file.not.found"),
    FILE_NAME_EXISTS("file.name.exist"),
    FILE_UPLOAD_FAILED("file.upload.failed"),
    FILE_DELETE_FAILED("file.delete.failed"),
    INVALID_FILE_TYPE("file.type.invalid"),
    FILE_STORE_ID_MISMATCH("file.store.id.mismatch"),
    FILE_NOT_APPROVED_YET("file.not.approved"),
    FILE_REJECTED("file.rejected"),
    FILE_TOO_LARGE("file.too.large"),
    CHANGING_FILE_SCOPE_NOT_ALLOWED("file.changing.scope.notAllowed"),
    FILE_SCOPE_MISMATCH("file.scope.mismatch"),
    CHANGING_FILE_MANAGER_NOT_ALLOWED("file.changing.manager.notAllowed"),
    FILE_SCOPE_NOT_FOUND("file.scope.notFound"),
    FILE_MANAGED_BY_NOT_FOUND("file.managedBy.notFound"),
    FILE_IN_USE("file.in.use"),
    FILE_IN_USE_VALIDATION_FAILED("file.inUse.validationFailed"),


    // ==================== Folder VALIDATION ====================
    FOLDER_ID_NOT_NULL("folder.id.notnull"),
    FOLDER_ID_NULL("folder.id.null"),
    FOLDER_ID_POSITIVE("folder.id.positive"),
    FOLDER_NAME_NOT_BLANK("folder.name.notblank"),
    FOLDER_STORE_ID_NOT_BLANK("folder.storeId.notblank"),
    FOLDER_NAME_SIZE("folder.name.size"),
    FOLDER_PARENTID_POSITIVE("folder.parent.id.positive"),

    // ==================== Folder Error ====================
    FOLDER_NOT_FOUND("folder.not.found"),
    FOLDER_NAME_EXISTS("folder.name.exist"),
    FOLDER_HIERARCHY_CYCLIC("folder.hierarchy.cyclic"),
    FOLDER_STORE_ID_MISMATCH("folder.store.id.mismatch"),
    CHANGING_FOLDER_SCOPE_NOT_ALLOWED("changing.folder.scope.not.allowed"),
    ROOT_FOLDER_CREATION_NOT_ALLOWED("root.folder.creation.not.allowed"),
    FOLDER_SCOPE_MISMATCH("folder.scope.mismatch"),
    CHANGING_FOLDER_MANAGER_NOT_ALLOWED("changing.folder.manager.not.allowed"),
    FOLDER_SCOPE_NOT_FOUND("folder.scope.not.found"),
    FOLDER_MANAGED_BY_NOT_FOUND("folder.managed.by.not.found"),

    // ==================== HTTP Status Messages ====================
    HTTP_OK("http.ok"),
    HTTP_CREATED("http.created"),
    HTTP_NO_CONTENT("http.no.content"),

    HTTP_BAD_REQUEST("http.bad.request"),
    HTTP_UNAUTHORIZED("http.unauthorized"),
    HTTP_FORBIDDEN("http.forbidden"),
    HTTP_NOT_FOUND("http.not.found"),
    HTTP_CONFLICT("http.conflict"),

    HTTP_INTERNAL_SERVER_ERROR("http.internal.server.error"),
    HTTP_SERVICE_UNAVAILABLE("http.service.unavailable");

    //
    private final String key;
}
