package store.emall.backend.security.dto;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken
) {
    public static AuthTokenResponse bearer(String accessToken, String refreshToken) {
        return new AuthTokenResponse("Bearer", accessToken, refreshToken);
    }
}
