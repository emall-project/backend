package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MallStatsDto {
    private Long mallId;
    private String name;
    private String cityName;
    private String status;
    private Integer capacity;
    private long shopCount;
    private long activeShopCount;
    private long restaurantCount;
    private long serviceCount;
    private String capacityUsage;
}
