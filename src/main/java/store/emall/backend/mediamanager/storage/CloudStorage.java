package store.emall.backend.mediamanager.storage;

import java.io.InputStream;

public interface CloudStorage {

    /**
     * Uploads a file to the storage provider.
     *
     * @param key         the object key or path
     * @param data        the file input stream
     * @param contentType the MIME type
     * @return the key or identifier of the stored file
     */
    String upload(String key, InputStream data, String contentType);

    /**
     * Downloads a file from storage.
     *
     * @param key the object key
     * @return InputStream of the file
     */
    InputStream download(String key);

    /**
     * Deletes a file from storage.
     *
     * @param key the object key
     */
    void delete(String key);

    /**
     * Generates a URL to access the file.
     *
     * @param key the object key
     * @return public or signed URL
     */
    String generateUrl(String key);

    String generatePresignedUrl(String key);


    String generatePresignedUploadUrl(String key);

    boolean fileExist(String key);

}