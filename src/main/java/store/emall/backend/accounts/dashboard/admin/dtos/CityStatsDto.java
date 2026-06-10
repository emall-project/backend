package store.emall.backend.accounts.dashboard.admin.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityStatsDto {
    private Long cityId;
    private String name;
    private String baseFee;
    private boolean isActive;
    private long mallCount;
    private long shopCount;
}
