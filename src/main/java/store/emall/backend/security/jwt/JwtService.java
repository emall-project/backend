package store.emall.backend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.dto.StoreRef;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // ==================== Generate Tokens ====================

    // For ADMIN
    public String generateAccessToken(Long userId, String username, String fullName, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_USER_ID,    userId);
        claims.put(SecurityConstants.CLAIM_USERNAME,   username);
        claims.put(SecurityConstants.CLAIM_FULL_NAME,  fullName);
        claims.put(SecurityConstants.CLAIM_ROLE,       role);
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);
        return buildToken(claims, username, SecurityConstants.ACCESS_TOKEN_EXPIRATION_MS);
    }

    // For Customer
    public String generateAccessToken(Long userId, String username, String fullName,
                                      String role, Integer age, String gender) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_USER_ID,    userId);
        claims.put(SecurityConstants.CLAIM_USERNAME,   username);
        claims.put(SecurityConstants.CLAIM_FULL_NAME,  fullName);
        claims.put(SecurityConstants.CLAIM_ROLE,       role);
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);
        if (age    != null) claims.put(SecurityConstants.CLAIM_AGE,    age);
        if (gender != null) claims.put(SecurityConstants.CLAIM_GENDER, gender);
        return buildToken(claims, username, SecurityConstants.ACCESS_TOKEN_EXPIRATION_MS);
    }

    /**
     * For SHOP_OWNER — includes owned shop IDs.
     * Frontend reads shopIds from JWT and shows the "choose your shop" screen.
     */
    public String generateAccessToken(Long userId, String username, String fullName,
                                      String role, List<StoreRef> shopIds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_USER_ID,    userId);
        claims.put(SecurityConstants.CLAIM_USERNAME,   username);
        claims.put(SecurityConstants.CLAIM_FULL_NAME,  fullName);
        claims.put(SecurityConstants.CLAIM_ROLE,       role);
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);
        List<Map<String, Object>> storeList = shopIds != null
                ? shopIds.stream()
                .map(s -> Map.<String, Object>of("storeId", s.getStoreId(), "mallId", s.getMallId()))
                .toList()
                : List.of();
        claims.put(SecurityConstants.CLAIM_SHOP_IDS, storeList);
        return buildToken(claims, username, SecurityConstants.ACCESS_TOKEN_EXPIRATION_MS);
    }

    /**
     * Refresh token — minimal claims (userId + username only).
     * Does NOT contain role or shopIds for security reasons.
     * When refreshed, role/shopIds are re-loaded fresh from DB.
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_USER_ID, userId);
        claims.put(SecurityConstants.CLAIM_USERNAME, username);
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_REFRESH);
        return buildToken(claims, username, SecurityConstants.REFRESH_TOKEN_EXPIRATION_MS);
    }

    /**
     * Temp token for OTP flow — contains phone number only.
     */
    public String generateTempToken(String phoneNumber) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_PHONE, phoneNumber);
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_TEMP);
        return buildToken(claims, phoneNumber, SecurityConstants.TEMP_TOKEN_EXPIRATION_MS);
    }

    // ==================== Extract Claims ====================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Object raw = extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_USER_ID));
        if (raw instanceof Integer) return ((Integer) raw).longValue();
        if (raw instanceof Long) return (Long) raw;
        return null;
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_ROLE, String.class));
    }

    @SuppressWarnings("unchecked")
    public List<StoreRef> extractShopIds(String token) {
        Object raw = extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_SHOP_IDS));
        if (raw == null) return Collections.emptyList();
        List<?> list = (List<?>) raw;
        return list.stream()
                .map(item -> {
                    Map<?, ?> map = (Map<?, ?>) item;
                    Long storeId = toLong(map.get("storeId"));
                    Long mallId  = toLong(map.get("mallId"));
                    return new StoreRef(storeId, mallId);
                })
                .toList();
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class));
    }

    public String extractPhone(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_PHONE, String.class));
    }

    public String extractFullName(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_FULL_NAME, String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public Integer extractAge(String token) {
        Object raw = extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_AGE));
        if (raw instanceof Integer) return (Integer) raw;
        return null;
    }

    public String extractGender(String token) {
        return extractClaim(token, claims -> claims.get(SecurityConstants.CLAIM_GENDER, String.class));
    }

    private Long toLong(Object v) {
        if (v instanceof Integer) return ((Integer) v).longValue();
        if (v instanceof Long)    return (Long) v;
        return null;
    }

    // ==================== Validation ====================

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isAccessToken(String token) {
        return SecurityConstants.TOKEN_TYPE_ACCESS.equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return SecurityConstants.TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
    }

    public boolean isTempToken(String token) {
        return SecurityConstants.TOKEN_TYPE_TEMP.equals(extractTokenType(token));
    }

    // ==================== Internal ====================

    private String buildToken(Map<String, Object> claims, String subject, long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateResetToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_RESET);
        claims.put(SecurityConstants.CLAIM_USERNAME, username);
        return buildToken(claims, username, SecurityConstants.RESET_TOKEN_EXPIRATION_MS);
    }

    public boolean isResetToken(String token) {
        return SecurityConstants.TOKEN_TYPE_RESET.equals(extractTokenType(token));
    }
}