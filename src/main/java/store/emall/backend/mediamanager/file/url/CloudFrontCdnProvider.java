package store.emall.backend.mediamanager.file.url;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CloudFrontCdnProvider implements MediaCdnProvider {

    private static final String PROVIDER_NAME = "cloudfront";

    private final String cdnBaseUrl;
    private final String keyPairId;
    private final String privateKeyPem;
    private final long signedUrlTtlSeconds;
    private volatile PrivateKey privateKey;

    public CloudFrontCdnProvider(
            @Value("${media.cdn.base-url:}") String cdnBaseUrl,
            @Value("${media.cdn.key-pair-id:}") String keyPairId,
            @Value("${media.cdn.private-key:}") String privateKeyPem,
            @Value("${media.cdn.signed-url-ttl-seconds:300}") long signedUrlTtlSeconds
    ) {
        this.cdnBaseUrl = cdnBaseUrl;
        this.keyPairId = keyPairId;
        this.privateKeyPem = privateKeyPem;
        this.signedUrlTtlSeconds = signedUrlTtlSeconds;
    }

    @Override
    public String providerName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isConfigured() {
        return hasCdnBaseUrl()
                && keyPairId != null && !keyPairId.isBlank()
                && privateKeyPem != null && !privateKeyPem.isBlank();
    }

    @Override
    public String publicUrl(String objectKey) {
        return cdnUrl(objectKey);
    }

    @Override
    public Optional<String> signedUrl(String objectKey) {
        if (!isConfigured()) {
            return Optional.empty();
        }
        String resourceUrl = cdnUrl(objectKey);
        try {
            long expiresAt = Instant.now().plusSeconds(signedUrlTtlSeconds).getEpochSecond();
            String policy = cannedPolicy(resourceUrl, expiresAt);

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(getPrivateKey());
            signature.update(policy.getBytes(StandardCharsets.UTF_8));

            String encodedSignature = cloudFrontBase64(signature.sign());
            String separator = resourceUrl.contains("?") ? "&" : "?";
            return Optional.of(resourceUrl
                    + separator
                    + "Expires=" + expiresAt
                    + "&Signature=" + encodedSignature
                    + "&Key-Pair-Id=" + URLEncoder.encode(keyPairId, StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.warn("CloudFront URL signing failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private boolean hasCdnBaseUrl() {
        return cdnBaseUrl != null && !cdnBaseUrl.isBlank();
    }

    private String cdnUrl(String objectKey) {
        String normalizedBase = cdnBaseUrl.endsWith("/")
                ? cdnBaseUrl.substring(0, cdnBaseUrl.length() - 1)
                : cdnBaseUrl;
        String normalizedKey = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        return normalizedBase + "/" + normalizedKey;
    }

    private PrivateKey getPrivateKey() throws Exception {
        PrivateKey cached = privateKey;
        if (cached != null) {
            return cached;
        }
        synchronized (this) {
            if (privateKey == null) {
                privateKey = parsePrivateKey(privateKeyPem);
            }
            return privateKey;
        }
    }

    private String cannedPolicy(String resourceUrl, long expiresAt) {
        return "{\"Statement\":[{\"Resource\":\""
                + resourceUrl.replace("\\", "\\\\").replace("\"", "\\\"")
                + "\",\"Condition\":{\"DateLessThan\":{\"AWS:EpochTime\":"
                + expiresAt
                + "}}}]}";
    }

    private String cloudFrontBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes)
                .replace('+', '-')
                .replace('=', '_')
                .replace('/', '~');
    }

    private PrivateKey parsePrivateKey(String pemValue) throws Exception {
        String normalized = pemValue.replace("\\n", "\n").trim();
        boolean rsaPkcs1 = normalized.contains("BEGIN RSA PRIVATE KEY");
        String base64 = normalized
                .replaceAll("-----BEGIN [A-Z ]*PRIVATE KEY-----", "")
                .replaceAll("-----END [A-Z ]*PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] der = Base64.getDecoder().decode(base64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        if (!rsaPkcs1) {
            try {
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(der));
            } catch (Exception ignored) {
                // Some deployments store an RSA PKCS#1 key without a matching header.
            }
        }

        return keyFactory.generatePrivate(parsePkcs1PrivateKey(der));
    }

    private RSAPrivateCrtKeySpec parsePkcs1PrivateKey(byte[] der) {
        DerReader reader = new DerReader(der);
        reader.readSequence();
        reader.readInteger(); // version

        List<BigInteger> values = new ArrayList<>();
        while (reader.hasRemaining()) {
            values.add(reader.readInteger());
        }
        if (values.size() < 8) {
            throw new IllegalArgumentException("Invalid RSA private key");
        }
        return new RSAPrivateCrtKeySpec(
                values.get(0),
                values.get(1),
                values.get(2),
                values.get(3),
                values.get(4),
                values.get(5),
                values.get(6),
                values.get(7)
        );
    }

    private static final class DerReader {
        private final byte[] data;
        private int pos;
        private int sequenceEnd;

        private DerReader(byte[] data) {
            this.data = data;
            this.sequenceEnd = data.length;
        }

        private void readSequence() {
            int tag = readByte();
            if (tag != 0x30) {
                throw new IllegalArgumentException("Expected DER sequence");
            }
            sequenceEnd = pos + readLength();
        }

        private boolean hasRemaining() {
            return pos < sequenceEnd;
        }

        private BigInteger readInteger() {
            int tag = readByte();
            if (tag != 0x02) {
                throw new IllegalArgumentException("Expected DER integer");
            }
            int length = readLength();
            byte[] value = new byte[length];
            System.arraycopy(data, pos, value, 0, length);
            pos += length;
            return new BigInteger(1, value);
        }

        private int readLength() {
            int length = readByte();
            if ((length & 0x80) == 0) {
                return length;
            }
            int bytes = length & 0x7f;
            int result = 0;
            for (int i = 0; i < bytes; i++) {
                result = (result << 8) | readByte();
            }
            return result;
        }

        private int readByte() {
            if (pos >= data.length) {
                throw new IllegalArgumentException("Unexpected end of DER data");
            }
            return data[pos++] & 0xff;
        }
    }
}
