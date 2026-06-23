package store.emall.backend.mediamanager.file.service;

import store.emall.backend.mediamanager.file.dto.CompleteUploadRequest;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlRequest;
import store.emall.backend.mediamanager.file.dto.FileUploadByUrlResponse;

public interface MediaUploadService {

    FileUploadByUrlResponse uploadByUrl(FileUploadByUrlRequest request);

    FileUploadByUrlResponse uploadByUrl(Long shopId, FileUploadByUrlRequest request);

    FileUploadByUrlResponse createTempUploadUrl(FileUploadByUrlRequest request);

    void completeUpload(CompleteUploadRequest request);
}
