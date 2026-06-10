package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentUserDto {
    private Long userId;
    private String username;
    private String fullName;
    private String role;
    private String createdAt;
}
