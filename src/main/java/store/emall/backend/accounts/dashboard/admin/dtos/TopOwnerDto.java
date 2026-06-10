package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopOwnerDto {
    private Long userId;
    private String username;
    private String fullName;
    private long shopCount;
    private long activeShops;
}
