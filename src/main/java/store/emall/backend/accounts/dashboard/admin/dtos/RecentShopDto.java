package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentShopDto {
    private Long shopId;
    private String name;
    private String mallName;
    private String ownerUsername;
    private String status;
    private String createdAt;
}
