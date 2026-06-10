package store.emall.backend.accounts.dashboard.shopowner.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyShopRequestDto {
    private Long requestId;
    private String shopName;
    private String mallName;
    private String status;
    private String rejectionReason;
    private String submittedAt;
}
