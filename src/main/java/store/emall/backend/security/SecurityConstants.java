package store.emall.backend.security;

public final class SecurityConstants {

    private SecurityConstants() {}

    // ==================== JWT ====================
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";
    public static final String TEMP_TOKEN_HEADER = "X-Temp-Token";
    public static final String TOKEN_TYPE = "JWT";

    // ==================== Token Expiration ====================
    public static final long ACCESS_TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000;       // 24 hours
    public static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000;  // 7 days
    public static final long TEMP_TOKEN_EXPIRATION_MS = 5 * 60 * 1000;               // 5 minutes (for OTP verification)

    // ==================== Token Claims ====================
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_FULL_NAME = "fullName";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String CLAIM_PHONE = "phone";
    public static final String CLAIM_SHOP_IDS = "shopIds";
    public static final String CLAIM_AGE = "age";
    public static final String CLAIM_GENDER = "gender";

    // ==================== Token Types ====================
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";
    public static final String TOKEN_TYPE_TEMP = "TEMP";
    public static final String TOKEN_TYPE_RESET = "RESET";

    // ==================== Roles ====================
    public static final String ROLE_ADMIN      = "ROLE_ADMIN";
    public static final String ROLE_CUSTOMER   = "ROLE_CUSTOMER";
    public static final String ROLE_SHOP_OWNER = "ROLE_SHOP_OWNER";
    public static final String ROLE_INTERNAL   = "ROLE_INTERNAL";

    public static final String ROLE_ADMIN_SHORT      = "ADMIN";
    public static final String ROLE_CUSTOMER_SHORT   = "CUSTOMER";
    public static final String ROLE_SHOP_OWNER_SHORT = "SHOP_OWNER";

    // ==================== Public Endpoints ====================
    public static final String[] PUBLIC_URLS = {
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/phone/request-otp",
            "/api/auth/phone/verify-otp",
            "/api/auth/refresh-token",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/actuator/**",
            "/api/auth/forgot-password/request",
            "/api/auth/forgot-password/resend",
            "/api/auth/forgot-password/reset",
            "/api/subscriptions/webhooks/stripe",
            "/api/subscriptions/shop/*/status"
    };

    // ==================== OTP ====================
    public static final int OTP_LENGTH = 6;
    public static final long OTP_EXPIRATION_MS = 5 * 60 * 1000; // 5 minutes
    public static final long RESET_TOKEN_EXPIRATION_MS = 10 * 60 * 1000; // 10 minutes

}
