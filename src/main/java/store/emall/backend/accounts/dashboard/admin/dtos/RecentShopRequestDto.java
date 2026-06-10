package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentShopRequestDto {
    private Long id;
    private String ownerUsername;
    private String shopName;
    private String mallName;
    private String status;
    private String submittedAt;
}