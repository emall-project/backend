package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentRequestDto {
    private Long id;
    private String username;
    private String shopName;
    private String status;
    private String submittedAt;
}