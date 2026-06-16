package store.emall.backend.security.otp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.emall.backend.security.SecurityConstants;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory OTP store. For production, replace with Redis or database-backed storage.
 */
@Service
@Slf4j
public class OtpService {

    private final Map<String, OtpEntry> otpShop = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", secureRandom.nextInt(999999));

        OtpEntry entry = new OtpEntry(otp, System.currentTimeMillis() + SecurityConstants.OTP_EXPIRATION_MS);
        otpShop.put(phoneNumber, entry);

        // TODO: Integrate with SMS provider to actually send the OTP
        log.info("OTP generated for phone {}: {} (replace with SMS in production)", phoneNumber, otp);

        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpEntry entry = otpShop.get(phoneNumber);

        if (entry == null) {
            return false;
        }

        if (System.currentTimeMillis() > entry.expiresAt()) {
            otpShop.remove(phoneNumber);
            return false;
        }

        if (entry.otp().equals(otp)) {
            otpShop.remove(phoneNumber); // One-time use
            return true;
        }

        return false;
    }

    public boolean isOtpExpired(String phoneNumber) {
        OtpEntry entry = otpShop.get(phoneNumber);
        if (entry == null) return true;
        return System.currentTimeMillis() > entry.expiresAt();
    }

    public OtpVerifyResult verifyOtpWithReason(String key, String otp) {
        OtpEntry entry = otpShop.get(key);

        if (entry == null) {
            return OtpVerifyResult.EXPIRED;
        }

        if (System.currentTimeMillis() > entry.expiresAt()) {
            otpShop.remove(key);
            return OtpVerifyResult.EXPIRED;
        }

        if (entry.otp().equals(otp)) {
            otpShop.remove(key);
            return OtpVerifyResult.SUCCESS;
        }

        return OtpVerifyResult.INVALID;
    }


    private record OtpEntry(String otp, long expiresAt) {}
}
