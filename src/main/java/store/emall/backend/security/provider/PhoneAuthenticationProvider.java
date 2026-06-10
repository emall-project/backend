package store.emall.backend.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import store.emall.backend.security.userdetails.CustomUserDetailsService;

/**
 * Authenticates users after successful OTP verification.
 * At this point, the phone number has already been verified via OTP,
 * so we just need to load the user and check if the account is active.
 */
@Component
@RequiredArgsConstructor
public class PhoneAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = authentication.getName();

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            throw new BadCredentialsException("Phone number not registered");
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }

        return new PhoneAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
