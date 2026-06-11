package store.emall.backend.security.userdetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.emall.backend.security.SecurityConstants;
import store.emall.backend.security.dto.StoreRef;
import store.emall.backend.accounts.shop.ShopRepository;
import store.emall.backend.accounts.user.User;
import store.emall.backend.accounts.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        return buildUserDetails(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with phone: " + phoneNumber));

        return buildUserDetails(user);
    }

    /**
     * Builds CustomUserDetails.
     * For SHOP_OWNER: loads their shop IDs, so they are embedded in the JWT.
     * For ADMIN / CUSTOMER: shopIds is empty.
     */
    private CustomUserDetails buildUserDetails(User user) {
        if (SecurityConstants.ROLE_SHOP_OWNER.equals(user.getRole().getCode())) {
            List<StoreRef> storeRefs = shopRepository.findByOwner_UserId(user.getUserId())
                    .stream()
                    .map(shop -> new StoreRef(shop.getShopId(), shop.getMall().getMallId()))
                    .toList();
            return new CustomUserDetails(user, storeRefs);
        }
        if (SecurityConstants.ROLE_CUSTOMER.equals(user.getRole().getCode())) {
            return new CustomUserDetails(user, user.getAge(), user.getGender());
        }
        return new CustomUserDetails(user);
    }
}
