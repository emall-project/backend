package store.emall.backend.accounts.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private String role;
}