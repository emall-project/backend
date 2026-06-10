package store.emall.backend.accounts.dashboard.shopowner.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnedShopDto {
    private Long shopId;
    private String name;
    private String mallName;
    private String cityName;
    private String category;
    private String location;
    private String status;
    private String createdAt;
}
