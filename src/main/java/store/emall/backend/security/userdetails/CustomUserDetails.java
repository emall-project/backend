package store.emall.backend.security.userdetails;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import store.emall.backend.accounts.user.Gender;
import store.emall.backend.security.dto.StoreRef;
import store.emall.backend.accounts.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String fullName;
    private final String phoneNumber;
    private final String roleCode;
    private final boolean isActive;
    private final Collection<? extends GrantedAuthority> authorities;

    private final Integer age;
    private final Gender gender;

    private final List<StoreRef> shopIds;

    public CustomUserDetails(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.roleCode = user.getRole().getCode();
        this.isActive = Boolean.TRUE.equals(user.getIsActive());
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getCode()));
        this.age = null;
        this.gender = null;
        this.shopIds = Collections.emptyList();
    }

    public CustomUserDetails(User user, Integer age, Gender gender) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.roleCode = user.getRole().getCode();
        this.isActive = Boolean.TRUE.equals(user.getIsActive());
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getCode()));
        this.age = age;
        this.gender = gender;
        this.shopIds = Collections.emptyList();
    }

    public CustomUserDetails(User user, List<StoreRef> shopIds) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.phoneNumber = user.getPhoneNumber();
        this.roleCode = user.getRole().getCode();
        this.isActive = Boolean.TRUE.equals(user.getIsActive());
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getCode()));
        this.age = null;
        this.gender = null;
        this.shopIds = shopIds != null ? shopIds : Collections.emptyList();
    }

    public CustomUserDetails(Long userId,
                             String username,
                             String fullName,
                             String roleCode,
                             Integer age,
                             Gender gender,
                             List<StoreRef> shopIds) {
        this.userId = userId;
        this.username = username;
        this.password = null;
        this.fullName = fullName;
        this.phoneNumber = null;
        this.roleCode = roleCode;
        this.isActive = true;
        this.authorities = List.of(new SimpleGrantedAuthority(roleCode));
        this.age = age;
        this.gender = gender;
        this.shopIds = shopIds != null ? shopIds : Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
