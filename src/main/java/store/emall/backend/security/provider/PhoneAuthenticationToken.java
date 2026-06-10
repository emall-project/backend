package store.emall.backend.security.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom authentication token for phone number + OTP based authentication.
 */
public class PhoneAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;  // phone number or UserDetails
    private final Object credentials; // OTP code

    // Unauthenticated token (before OTP verification)
    public PhoneAuthenticationToken(String phoneNumber) {
        super(Collections.emptyList());
        this.principal = phoneNumber;
        this.credentials = null;
        setAuthenticated(false);
    }

    // Authenticated token (after OTP verification)
    public PhoneAuthenticationToken(Object principal, Object credentials,
                                    Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
