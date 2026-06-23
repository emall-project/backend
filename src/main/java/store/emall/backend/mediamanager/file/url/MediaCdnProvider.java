package store.emall.backend.mediamanager.file.url;

import java.util.Optional;

public interface MediaCdnProvider {

    String providerName();

    boolean isConfigured();

    String publicUrl(String objectKey);

    Optional<String> signedUrl(String objectKey);
}
